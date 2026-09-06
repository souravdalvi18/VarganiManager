# Vargani Manager

Android app for **🚩 । छावा प्रतिष्ठान । 🚩**

## Version 1
- Vargani collection records
- Cash / Online payment mode
- Automatic total collection, total expenses and remaining balance
- Cash/Online collection breakdown
- Custom expense names typed by the admin
- Contributor and expense lists
- WhatsApp or SMS message choice after saving a contribution
- English / Marathi UI toggle
- Offline local storage on one phone
- Reports / summary screen

## Build
The project uses Android Gradle Plugin 8.13.0 and Gradle 8.13, with JDK 17 and Android API 35. Android's current compatibility guidance lists AGP 8.13 with Gradle 8.13 and JDK 17.

### Android Studio
Open the project folder in Android Studio, let it sync/download the required Gradle and Android SDK components, then choose:
`Build > Build APK(s)`

### GitHub Actions
The included `.github/workflows/build.yml` can build the debug APK automatically on GitHub. The generated APK is uploaded as a workflow artifact.

## Note
This environment does not have the Android SDK/build tools installed and does not have outbound package-download access, so I cannot truthfully attach a compiled APK from this sandbox. The ZIP is the complete build-ready source project; the GitHub Actions workflow is included specifically to produce the APK.
