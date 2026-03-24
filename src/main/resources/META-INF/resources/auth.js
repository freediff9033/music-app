// This function handles both Login and Sign Up depending on the 'path' we send it
async function handleAuth(path) {

    alert("The button works! Sending to: " + path)


    // 1. Grab the values from the HTML input boxes
    const usernameInput = document.getElementById('username').value;
    const passwordInput = document.getElementById('password').value;
    const errorDisplay = document.getElementById('error-msg');

    // 2. Simple validation: Don't even talk to Java if the boxes are empty
    if (!usernameInput || !passwordInput) {
        alert("Please enter both a username and a password.");
        return;
    }

    // 3. Create the 'User' object to send to Java
    const userData = {
        username: usernameInput,
        password: passwordInput
    };

    try {
        // 4. THE CONNECTION: Fetch sends the data to your UserResource.java
        const response = await fetch(path, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData) // Turn the JS object into a JSON string
        });

        // 5. Handle the response from Java
        if (response.ok) {
            // Success! Save the username in the browser's memory
            localStorage.setItem('loggedInUser', usernameInput);
            
            // Call the function to switch the screen
            showApp();
        } else if (response.status === 409) {
            errorDisplay.innerText = "Error: That username is already taken!";
        } else {
            errorDisplay.innerText = "Error: Invalid username or password.";
        }
    } catch (error) {
        console.error("Connection failed:", error);
        errorDisplay.innerText = "Server is down. Is Quarkus running?";
    }
}

// This function hides the login screen and shows the "Welcome" screen
function showApp() {
    const user = localStorage.getItem('loggedInUser');
    
    // Hide the login box
    document.getElementById('login-section').style.display = 'none';
    
    // Show the main app area
    document.getElementById('main-app').style.display = 'block';
    
    // Put the name on the screen
    document.getElementById('user-display').innerText = "Welcome, " + user + "!";
}
// Add this to the bottom of auth.js
function logout() {
    // 1. Clear the "Memory" of the browser
    localStorage.removeItem('loggedInUser');
    
    // 2. Force the page to refresh (this sends them back to the login screen)
    window.location.reload();
}

// Check if the user is already logged in when they refresh the page
window.onload = function() {
    if (localStorage.getItem('loggedInUser')) {
        showApp();
    }
};

async function uploadSong() {
    const fileInput = document.getElementById('songFile');
    const title = document.getElementById('songTitle').value;
    const artist = document.getElementById('songArtist').value;
    const genre = document.getElementById('songGenre').value;

    if (fileInput.files.length === 0) {
        alert("Please select an MP3 file first!");
        return;
    }

    // Create the "Envelope" (FormData)
    const formData = new FormData();
    formData.append('file', fileInput.files[0]);
    formData.append('fileName', fileInput.files[0].name);
    formData.append('title', title);
    formData.append('artist', artist);
    formData.append('genre', genre);

    try {
        const response = await fetch('/songs/upload', {
            method: 'POST',
            body: formData // Note: Do NOT set Content-Type header; the browser does it automatically for FormData
        });

        if (response.ok) {
            alert("Upload successful! Your song is now in the cloud.");
            location.reload(); // Refresh to see the new song
        } else {
            alert("Upload failed. Check the Java console.");
        }
    } catch (error) {
        console.error("Error:", error);
    }
};

// 1. Load the songs when the page opens
window.onload = loadSongs;

async function loadSongs() {
    try {
        const response = await fetch('/songs');
        const songs = await response.json();
        const tableBody = document.getElementById('songTableBody');
        tableBody.innerHTML = ''; // Clear old list

        songs.forEach(song => {
            const playBtn = `<button onclick="playMusic('${song.filePath}', '${song.title}')">▶ Play</button>`;
            tableBody.innerHTML += `
                <tr>
                    <td>${song.title}</td>
                    <td>${song.artist}</td>
                    <td>${song.genre}</td>
                    <td>${song.uploadedBy || 'Guest'}</td>
                    <td>
                        <button onclick="playMusic('${song.filePath}', '${song.title}')">▶ Play</button>
                    </td>
                </tr>
            `;
        });
    } catch (error) {
        console.error("Error loading library:", error);
    }
}

// 2. The Player Logic
function playMusic(path, title) {
    const player = document.getElementById('main-player');
    const source = document.getElementById('audio-source');
    const container = document.getElementById('player-container');
    const label = document.getElementById('now-playing');

    // 1. Update the title and show the player bar
    label.innerText = "Now Playing: " + title;
    container.style.display = "block";

    // 2. Point the audio source to the file path (e.g., /music-files/1711234_song.mp3)
    source.src = path;

    // 3. Important: Tell the browser to reload and start the music
    player.load(); 
    player.play().catch(e => console.error("Playback failed:", e));
}