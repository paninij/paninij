# Releasing the Project

This project's build system uses the Gradle plugin `com.bmuschko.nexus` to
configure both signing and uploading release archives to maven central. This
can be performed via `./gradlew --no-daemon clean uploadArchives`. Note that
`--no-daemon` is only needed to support interactive input from the console,
for example, to type in secret credentials/passwords.

To upload archives, the `com.bmuschko.nexus` plugin needs information about
each of the following project properties:

- `signing.keyId`               (required)
- `signing.secretKeyRingFile`   (required)
- `signing.password`            (optional)
- `nexusUsername`               (optional)
- `nexusPassword`               (optional)

None of these properties should be committed to the repository for one of two
reasons:

- A configuration is developer specific (e.g. `signing.secretKeyRingFile`).
- A configuration is secret (e.g. `signing.password` and `nexusPassword`).

If an "optional" property is not set on the project when the `uploadArchives`
task is to be run, then the Nexus plugin will interactively prompt the user for
this information during the build.

So, to release the project to Maven Central these properties, a developer needs
to somehow privately configure these properties on the developer's development
system. We expect these project properties to be set in an auxiliary build
script, `releasing.gradle`, in the root of the repository.

Some notes on the `releasing.gradle` script:

- If this script exists, then it will be invoked and applied to this project.
  If it does not exist, then attempts to upload archives should fail.
- Because this script may contain sensitive information, it should never be
  committed to the Git repository.
- The script should set the desired project properties using the project's
  `ext` configuration, that is, the project's `ExtraPropertiesExtension`.

So, the contents of `releasing.gradle` may look something like this:

``` groovy
ext.set('nexusUsername', 'XXXXXXX')
ext.set('signing.secretKeyRingFile', '/home/username/.gnupg/secring.gpg')
ext.set('signing.keyId', 'XXXXXXX')
```

Or you might add the passwords as well to prevent password input from the
command line. (This is only recommended if you have trouble inputting the
passwords on the command line.)

``` groovy
ext.set('nexusUsername', 'XXXXXXX')
ext.set('nexusPassword', 'XXXXXXXXXXXXXXXXXX')
ext.set('signing.secretKeyRingFile', '/home/username/.gnupg/secring.gpg')
ext.set('signing.keyId', 'XXXXXXX')
ext.set('signing.password', 'XXXXXXXXXXXXXXXXX')
```
