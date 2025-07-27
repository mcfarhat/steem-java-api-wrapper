![SteemJ Logo](https://camo.githubusercontent.com/a325dd7ebceee15b8ca3fd57383c4e8330cc0425/687474703a2f2f696d6775722e636f6d2f784a4c514e31752e706e67)

This project allows you to easily access data stored in the Steem blockchain. The project has been initialized by <a href="https://steemit.com/@dez1337">dez1337 on steemit.com</a>.

#### The latest 0.4.x STABLE can be obtained via jitpack.io:

[![](https://jitpack.io/v/marvin-we/steem-java-api-wrapper.svg)](https://jitpack.io/#marvin-we/steem-java-api-wrapper/0.4.6-20180926-01PRE/steemj-core)

Example below is for latest 0.4.x. Visit https://jitpack.io/#marvin-we/steem-java-api-wrapper/0.4.6-20180926-01PRE/steemj-core to get a list of available builds.

## Gradle
```Gradle
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
    dependencies {
	        compile 'com.github.marvin-we.steem-java-api-wrapper:steemj-core:0.4.x-SNAPSHOT'
	}
```

## Maven
File: <i>pom.xml</i>
```Xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
    ...
	<dependency>
	    <groupId>com.github.marvin-we.steem-java-api-wrapper</groupId>
	    <artifactId>steemj-core</artifactId>
	    <version>0.4.x-SNAPSHOT</version>
	</dependency>
```

# Full Documentation
- Please have a look at the [Wiki](https://github.com/marvin-we/steem-java-api-wrapper/wiki) for full documentation, examples, operational details and other information.
- Or have a look at the JavaDoc.

# Communication
- Please contact me at the [Discord Java Channel](https://discord.gg/fsJjr3Q)
- Or directly on [Steemit.com](https://steemit.com/@dez1337)
- Beside that you can also create an [Issue](https://github.com/marvin-we/steem-java-api-wrapper/issues) here at GitHub.

# Contributors
- [philip-healy](https://github.com/philip-healy) took care of the "simplified operations".
- [ray66rus](https://steemit.com/@ray66rus) is testing SteemJ for Android and provided a lot of improvements.
- [inertia](https://steemit.com/@inertia) provided a bunch of unit tests to this project.
- An article from [Kyle](https://steemit.com/@klye) has been used to improve the documentation of the methods.
- The [guide](https://steemit.com/steem/@xeroc/steem-transaction-signing-in-a-nutshell) from [xeroc](https://steemit.com/@xeroc) shows how to create and sign transactions.

# Binaries
SteemJ binaries are pushed into the maven central repository and can be integrated with a bunch of build management tools like Maven.

Please have a look at the [Wiki](https://github.com/marvin-we/steem-java-api-wrapper/wiki/How-to-add-SteemJ-to-your-project) to find examples for Maven, Ivy, Gradle and others.

# Development Setup

Follow these instructions to set up a local development environment for contributing to the project.

### 1. Prerequisites

Before you begin, ensure you have the following software installed on your system:
-   **Git:** For version control.
-   **Java Development Kit (JDK) 8:** The project is built on Java 8. Newer versions may cause compilation issues.
-   **Apache Maven:** For dependency management and building the project.

### 2. Fork and Clone the Repository

1.  **Fork the Repository:** First, create a fork of the main repository (`mcfarhat/steem-java-api-wrapper`) to your own GitHub account.

2.  **Clone Your Fork:** Clone your personal fork to your local machine. Replace `your-username` with your actual GitHub username.
    ```bash
    git clone https://github.com/your-username/steem-java-api-wrapper.git
    cd steem-java-api-wrapper
    ```

3.  **Configure Remotes:** Set up a remote reference to the original repository (`upstream`). This is crucial for keeping your fork synchronized with the main project.
    ```bash
    git remote add upstream https://github.com/mcfarhat/steem-java-api-wrapper.git
    ```
    Verify the remotes are configured correctly by running `git remote -v`. The output should show your fork as `origin` and the main repository as `upstream`.

### 3. Building the Project

The project uses Maven to manage the build process.

**Important:** The full test suite contains legacy integration tests that are currently broken. To successfully build the project, you **must skip the tests** during the initial build.

-   Run the build command from the root directory:
    ```bash
    mvn clean install -DskipTests
    ```
    This command will download all required dependencies and compile the entire project.

### 4. Running a Single Test

When working on a new feature, you must prove your changes work by creating and running a dedicated test. Since the full test suite cannot be run, use the following command to run *only* your specific test file.

-   Replace `TestYourClassName` with the name of your test file:
    ```bash
    mvn test -Dtest=TestYourClassName
    ```
    For example, to run the test for the `block_api.get_block` endpoint, you would use:
    ```bash
    mvn test -Dtest=TestGetBlock
    ```
A successful run of your new test is required for your Pull Request to be approved.

### 5. IDE Setup (VSCode Example)

-   Open the project's root folder in Visual Studio Code.
-   Ensure you have the **"Extension Pack for Java"** from Microsoft installed. It will automatically detect the Maven `pom.xml` files and configure the project correctly.

# Bugs and Feedback
For bugs or feature requests please create a [GitHub Issue](https://github.com/marvin-we/steem-java-api-wrapper/issues). 

For general discussions or questions you can also:
* Post your questions in the [Discord Java Channel](https://discord.gg/9jZQHv)
* Reply to one of the SteemJ update posts on [Steemit.com](https://steemit.com/@dez1337)
* Contact me on [steemit.chat](https://steemit.chat/channel/dev)

# Example
The [sample module](https://github.com/marvin-we/steem-java-api-wrapper/tree/master/sample) of the SteemJ project provides showcases for the most common acitivies and operations users want to perform. 

Beside that you can find a lot of snippets and examples in the different [Wiki sections](https://github.com/marvin-we/steem-java-api-wrapper/wiki).  