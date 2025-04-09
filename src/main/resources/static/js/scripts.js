function setCookie(name, value, days) {
    const d = new Date();
    d.setTime(d.getTime() + (days * 24 * 60 * 60 * 1000));
    const expires = "expires=" + d.toUTCString();
    document.cookie = name + "=" + value + ";" + expires + ";path=/";
}

function getCookie(name) {
    const nameEQ = name + "=";
    const ca = document.cookie.split(';');
    for (let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) === ' ') c = c.substring(1, c.length);
        if (c.indexOf(nameEQ) === 0) return c.substring(nameEQ.length, c.length);
    }
    return null;
}

function scrollToBottom() {
    const messageContainer = document.querySelector('.custom-background-message');
    messageContainer.scrollTop = messageContainer.scrollHeight;
}

function saveUsername() {
    const username = document.getElementById("username").value;
    setCookie("username", username, 7);
}

/*function loadUsername() {
    const username = getCookie("username");
    if (username) {
        document.getElementById("message-user").value = username;
    }
}*/
/*
function fetchMessages() {
    const token = localStorage.getItem('authToken');
    if (token) {
        fetch('http://localhost:8080/messages', {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        })
        .then(response => response.json())
        .then(data => {
            const messagesContainer = document.querySelector('.custom-background-message ul');
            messagesContainer.innerHTML = '';
            data.forEach(message => {
                const messageElement = document.createElement('li');
                messageElement.className = 'list-group-item';
                messageElement.innerHTML = `<p>${message.user}</p><p>${message.message}</p>`;
                messagesContainer.appendChild(messageElement);
                scrollToBottom();
            });
        })
        .catch(error => console.error('Error fetching messages:', error));
    }
}*/

function fetchChatMessages() {
    const token = localStorage.getItem('authToken'); // Retrieve the auth token
    chatName = document.getElementById('chat-name').value;
    if (token) {
        fetch(`/api/chat?chatName=${encodeURIComponent(chatName)}`, {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        })
        .then(response => response.json())
        .then(data => {
            const messagesContainer = document.querySelector('.custom-background-message ul');
            messagesContainer.innerHTML = '';
            data.forEach(message => {
                const messageElement = document.createElement('li');
                messageElement.className = 'list-group-item';
                messageElement.innerHTML = `<p>${message.user}</p><p>${message.message}</p>`;
                messagesContainer.appendChild(messageElement);
                scrollToBottom();
            });
            console.log('Fetched chat messages:', data);
        })
        .catch(error => {
            console.error('Error fetching chat messages:', error);
        });
    } else {
        console.error('No auth token found');
    }
}

function fetchUsers() {
    const token = localStorage.getItem('authToken');
    if (token) {
        fetch('http://localhost:8080/users', {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        })
        .then(response => response.json())
        .then(data => {
            console.log('Fetched users:', data);
            const usersContainer = document.querySelector('.custom-background-user ul');
            usersContainer.innerHTML = '';
            data.forEach(user => {
                const userElement = document.createElement('li');
                userElement.className = 'list-group-item';
                userElement.innerHTML = `<p>${user}</p>`;
                usersContainer.appendChild(userElement);
            });
        })
        .catch(error => console.error('Error fetching users:', error));
    }
}

document.addEventListener('DOMContentLoaded', function () {
    const messageForm = document.querySelector('#messageForm');
    messageForm.addEventListener('submit', function (event) {
        event.preventDefault();

        const token = localStorage.getItem('authToken');

        if (token) {
        /*
            const messageData = {
                user: document.getElementById('message-user').value,
                message: document.getElementById('message-message').value
            };
            console.log('Message data:', messageData);
            fetch('/saveMessage', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + token
                },
                body: JSON.stringify(messageData)
            })
            .then(response => {
                if (response.ok) {
                    console.log('Message saved successfully');
                    //return response.json();
                } else {
                    throw new Error('Failed to save message: ' + response.status);
                }
            })
            .then(data => {
                console.log('Message saved successfully', data);
                // Optionally, refresh the messages or update the UI
                //fetchMessages();
            })
            .catch(error => {
                console.error('Error saving message', error);
            });*/


            const messageChatData = {
                    user: document.getElementById('message-user').value,
                    message: document.getElementById('message-message').value,
                    chatName: document.getElementById('chat-name').value
            };

            console.log('Message data:', messageChatData);
            fetch('/api/chatAdd', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + token
                },
                body: JSON.stringify(messageChatData)
            })
            .then(response => {
                if (response.ok) {
                console.log('Message saved successfully');
            //return response.json();
                } else {
                    throw new Error('Failed to save message: ' + response.status);
                }
            })
            .then(data => {
            console.log('Message saved successfully', data);
            // Optionally, refresh the messages or update the UI
            //fetchMessages();
            fetchChatMessages();
            })
            .catch(error => {
                console.error('Error saving message', error);
            });

        } else {
            console.error('No auth token found');
        }
    });
});



document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('button-custom-chat-create').addEventListener('click', function() {
        const chatName = document.getElementById('chat-name').value;
        const token = localStorage.getItem('authToken');

        if (token) {
            fetch('http://localhost:8080/api/chatCreate', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'Authorization': 'Bearer ' + token
                },
                body: new URLSearchParams({
                    'name': chatName
                })
            })
            .then(response => response.json())
            .then(data => {
                console.log('Success:', data);
                document.getElementById('chat-name-value').textContent = data.chatName;
            })
            .catch((error) => {
                console.error('Error:', error);
            });
        } else {
            console.error('No auth token found');
        }
    });
});

window.onload = function() {
    //loadUsername();
    fetchUsers();
    scrollToBottom();
    //setInterval(fetchMessages, 5000);
    setInterval(fetchChatMessages, 5000);
}