package cordova.plugin.permissionplugin;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PermissionPlugin extends CordovaPlugin {

	static CallbackContext sCallbackContext = null;
	static int countPermission = 0;

	@Override
	public boolean execute(final String action, final CordovaArgs args, final CallbackContext callbackContext) throws JSONException {
		try {
			sCallbackContext = callbackContext;
			cordova.getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						if(action!=null) {
							if(action.equalsIgnoreCase("requestPermission")) {
								countPermission = args.getInt(1);
								checkIfUserGrantedPermissions(args);
							} else if(action.equalsIgnoreCase("openPermissionSettings")) {
								Intent intent = new Intent();
								intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
								Uri uri = Uri.fromParts("package", cordova.getActivity().getPackageName(), null);
								intent.setData(uri);
								cordova.getActivity().startActivity(intent);
							}
						}
					} catch (JSONException e) {
						e.toString();
					}
				}
			});
			return true;
        } catch(Exception ex) {
        	Log.e("PP::execute:",ex.toString());
        }
        return false;
	}

	@TargetApi(Build.VERSION_CODES.M)
	private boolean checkIfUserGrantedPermissions(CordovaArgs args) {
		try {
			JSONArray arrPermission = args.getJSONArray(0);
			JSONArray jsonArray = new JSONArray();
			for(int j=0;j<arrPermission.length();j++) {
				jsonArray.put(arrPermission.get(j));
			}
			String[] permissions = toArray(jsonArray);
			for(int i=0;i<permissions.length;i++) {
				cordova.requestPermissions(this, i, permissions);
			}
		} catch(Exception ex) {
			Log.e("PP:checkIfUserGranted:",ex.toString());
		}

		return true;
	}

	@TargetApi(Build.VERSION_CODES.M)
	@Override
	public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
		super.onRequestPermissionResult(requestCode, permissions, grantResults);
			try {
				JSONObject message = new JSONObject();
				int n = permissions.length;
				for (int i = 0; i < n; i++) {
					String permission = permissions[i];
					if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
						boolean showRationale = cordova.getActivity().shouldShowRequestPermissionRationale( permission );
						if (! showRationale) {
							message.put(String.valueOf(permissions[i]), "2");
						} else {
							message.put(String.valueOf(permissions[i]), "1");
						}
					} else if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
						message.put(String.valueOf(permissions[i]), "0");
					}

				}
				if(permissions.length==countPermission) {
					sCallbackContext.success(message);
				}
			} catch (Exception e) {
				sCallbackContext.error(e.getMessage());
				Log.e("PP:onRequestPermission:",e.toString());
			}

	}

	private void sendErrorCallback(final String errorMsg, final CallbackContext callbackContext) {
		cordova.getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				callbackContext.error(errorMsg);
			}
		});
	}

	private void sendSuccessCallback(final CallbackContext callbackContext) {
		cordova.getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
					callbackContext.sendPluginResult(pluginResult);
				} catch (Exception e) {
					Log.e("PP::successCallback:",e.toString());
				}
			}
		});
	}

	private String[] toArray(JSONArray permissions) {
		int n = permissions.length();
		String[] array = new String[n];
		for (int i = 0; i < n; i++) {
			array[i] = permissions.optString(i);
		}
		return array;
	}
}






