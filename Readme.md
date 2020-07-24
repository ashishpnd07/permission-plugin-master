title: cordova-plugin-permission.
description: this plugin for runtime permission;


calling example 1:
cordova.exec(function(success) {
    console.log(success)
},function(error) {
    console.log(error)
},"PermissionPlugin","requestPermission",[["android.permission.READ_CONTACTS", "android.permission.READ_PHONE_STATE", "android.permission.READ_SMS", "android.permission.SEND_SMS", "android.permission.ACCESS_FINE_LOCATION"],5]);


calling example 2:
cordova.exec(function(success) {
    console.log(success)
},function(error) {
    console.log(error)
},"PermissionPlugin","requestPermission",[["android.permission.READ_CONTACTS"],1]);



0 -> permission allowed , 1 - permission denied , 2 - permission denied with never ask again

add permissions in menifest file whichever is required
