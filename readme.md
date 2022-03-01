# Utils

Utils is a Android library to avoid repetitive code.
 - Permissions

## Installation

Use gradle:
- settings.gradle
```gradle
    repositories {
        maven { url 'https://jitpack.io' }
       
    }
```
- build.gradle (app)
```gradle
    dependencies {
	        implementation 'com.github.matt2393.LibreriasMatt:permissions:0.6'
	}
```
## Require
Require activity-ktx and fragment ktx
- build.gradle (app)
```gradle
    dependencies {
	        implementation "androidx.activity:activity-ktx:$version-ktx"
            implementation "androidx.fragment:fragment-ktx:$version-ktx"
	}
```
## Usage
- Location, request permission and activate GPS

Require initialize in onCreate or onCreateView
```kotlin
    LocPermission.init(Activity or Fragment)
```
Request Location permissions and gps active
```kotlin
    LocPermission.launch(
        success = {
            // success permission and active GPS
        },
        error = { err ->
            // denied the permission or denied activate GPS
            // err.name = LocPermission.ErrorType.PERMISSION  -> denied the permission
            // err.name = LocPermission.ErrorType.GPS -> denied activate GPS
        }
    )
```

- All permissions, example:

Require initialize in onCreate or onCreateView
```kotlin
    ReqPermission.init(Activity or Fragment)
```
Require permission array, example
```kotlin
    val permissionsArray = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
```
Request permissions
```kotlin
    
    ReqPermission.launch(
        permission = permissionsArray,
        result =  { res ->
            // res = Map <String, Boolean>
            // String -> name permission, example Manifest.permission.CAMERA
            // Boolean -> true: pemission granted, false: permission denied
        }
    )
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT](https://choosealicense.com/licenses/mit/)