
<div align="center">
<h1> <a href="https://github.com/luciferro01/RepoQuest">RepoQuest</a> </h1>

![RepoQuest](https://socialify.git.ci/luciferro01/RepoQuest/image?custom_description=Idea+Plugin+that+provides+a+tool+window+for+searching+dependencies+from+Mvn+%26+NPM+%F0%9F%9A%80&description=1&font=Raleway&language=1&name=1&owner=1&pattern=Plus&theme=Dark)
<br/>

<!-- ![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?logo=kotlin&logoColor=white) -->

</div>

## Overview

**RepoQuest** is an IntelliJ IDEA plugin designed to provide a tool window for searching dependencies from Maven and NPM repositories.

[//]: # (## Screenshots)

[//]: # (![Maven-Image]&#40;https://i.postimg.cc/k5kZkZgs/Screenshot-2025-02-19-at-12-41-09-PM.png&#41;])

[//]: # (![Dependency-Dialog]&#40;https://i.postimg.cc/xdbpyR3z/Dependency-Dialog.png&#41;])

[//]: # (![Npm-Panel.png]&#40;https://i.postimg.cc/SNNZtqGq/Npm-Panel.png&#41;])

## Screenshots
<div style="display: flex; flex-wrap: wrap;">
  <img src="https://i.postimg.cc/k5kZkZgs/Screenshot-2025-02-19-at-12-41-09-PM.png" alt="Maven-Image" style="max-width: 30%; margin: 10px;">
  <img src="https://i.postimg.cc/xdbpyR3z/Dependency-Dialog.png" alt="Dependency-Dialog" style="max-width: 30%; margin: 10px;">
  <img src="https://i.postimg.cc/SNNZtqGq/Npm-Panel.png" alt="Npm-Panel" style="max-width: 30%; margin: 10px;">
</div>

## Features

### 1. 🚀 Dependency Search

- Search for dependencies from Maven and NPM repositories.
- View detailed information about each dependency.

### 2. 🎨 Customization

- Customize the appearance of the tool window.
- Configure search settings to suit your needs.

### 3. 📋 User Interface

- Intuitive and user-friendly interface.
- Smooth animations and transitions.

### 4. 🔒 Privacy-Focused

- All data is stored locally on your device.
- No external servers or third-party apps are used, ensuring maximum privacy and security.

## Project Structure

```
RepoQuest/
├── src/
│   ├── main/
│   │   ├── java/
│   │   ├── kotlin/
│   │   └── resources/
│   └── test/
├── build.gradle.kts
├── gradle.properties
└── README.md
```

## Files

### Core Components

- `src/main/kotlin/com/mohil_bansal/repo_quest/MainWindow.kt`: Main tool window implementation.
- `src/main/resources/META-INF/plugin.xml`: Plugin configuration file.
- `build.gradle.kts`: Gradle build script.
- `gradle.properties`: Gradle properties file.

## Installation

1. Clone the repository or download it as a ZIP file.
   ```bash
   git clone https://github.com/luciferro01/RepoQuest.git
   ```
2. Open the project in IntelliJ IDEA.
3. Build the project using Gradle.
4. Run the plugin.

## Usage

1. Launch IntelliJ IDEA with the plugin installed.
2. Open the RepoQuest tool window.
3. Search for dependencies and view their details.

## Development

### Prerequisites

- IntelliJ IDEA
- Gradle
- Kotlin

### Building

This plugin is designed to work out of the box. If you want to modify the code:

1. Edit the Kotlin files as required.
2. Build the project using `./gradlew build` to test your changes.

## Contribution

Feel free to fork the repository, create a new branch, and submit a pull request with your improvements.

## License

This project is licensed under the [GNU Lesser General Public License](https://www.gnu.org/licenses/lgpl-3.0.en.html) - see the LICENSE file for details.
## Author

Developed by [luciferro01](https://github.com/luciferro01/).
