# StreamFlow Music App

StreamFlow Music App is a lightweight and efficient music player application developed by **Team Odyssey**. It allows users to create, manage, and play songs from playlists with features like playback control, shuffle, history tracking, and song filtering.

## Table of Contents
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Commands](#commands)
- [Project Structure](#project-structure)
- [Dependencies](#dependencies)
- [Contributing](#contributing)
- [License](#license)

## Features
- **Music Playback**: Play, pause, resume, and stop songs.
- **Playlist Management**: Create, delete, and switch playlists.
- **Search & Filter**: Search songs by name, artist, genre, and album.
- **Shuffle & Speed Control**: Toggle shuffle and adjust playback speed.
- **Favorites & History**: Save songs to favorites and view playback history.
- **Authentication**: User login functionality.

## Installation
### Prerequisites
Ensure you have the following installed:
- Java 21 or later
- Apache Maven

### Steps
1. Clone the repository:
   ```sh
   git clone https://github.com/your-repo/streamflow-music-app.git
   cd streamflow-music-app
   ```
2. Build the project using Maven:
   ```sh
   mvn clean install
   ```
3. Run the application:
   ```sh
   java -jar target/StreamFlow-1.0-SNAPSHOT.jar
   ```

## Usage
Upon running the application, you will be prompted to log in. Once authenticated, you can use various commands to control playback and manage playlists.

## Commands
- `n` → Next song
- `b` → Previous song
- `p` → Pause
- `r` → Resume
- `s` → Search song
- `c` → Shuffle Play
- `f` → Add to favorites
- `l` → List favorites
- `h` → Show playback history
- `create [playlist]` → Create a new playlist
- `delete [playlist]` → Delete a playlist
- `switch [playlist]` → Switch to a different playlist
- `add song` → Add a song to a playlist
- `remove song` → Remove a song from a playlist
- Press **Enter** to stop

## Project Structure
```
streamflow-music-app/
├── README.md
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/odyssey/
│   │   │   ├── Main.java
│   │   │   ├── components/
│   │   │   ├── controllers/
│   │   │   ├── filters/
│   │   │   ├── services/
│   │   └── resources/
│   ├── test/java/com/odyssey/AppTest.java
```

## Dependencies
This project uses the following dependencies:
- **JUnit** (3.8.1) for testing
- **JLayer** (1.0.1) for MP3 playback

## Contributing
We welcome contributions! To contribute:
1. Fork the repository
2. Create a new branch (`git checkout -b feature-branch`)
3. Commit your changes (`git commit -m 'Add new feature'`)
4. Push to the branch (`git push origin feature-branch`)
5. Create a Pull Request

## License
This project is licensed under the **MIT License**.

