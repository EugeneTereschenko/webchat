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



function fetchChatMessages() {
    const token = localStorage.getItem('authToken'); // Retrieve the auth token
    chatName = document.getElementById('chat-name').value;
    if (token) {
        fetch(`/chat/api/chat?chatName=${encodeURIComponent(chatName)}`, {
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


function fetchUser(){
    const token = localStorage.getItem('authToken');
    chatName = document.getElementById('chat-name').value;
    if (token) {
        fetch(`/chat/api/user?chatName=${encodeURIComponent(chatName)}`, {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        })
        .then(response => response.json())
        .then(data => {
            console.log('Fetched user data:', data);

            const userMessageElement = document.querySelector('#message-user');
            userMessageElement.value = data.username;
        })
        .catch(error => console.error('Error fetching user data:', error));
    } else {
        console.error('No auth token found');
    }
}

function fetchUsers() {
    const token = localStorage.getItem('authToken');
    chatName = document.getElementById('chat-name').value;
    if (token) {
        fetch(`/chat/api/users?chatName=${encodeURIComponent(chatName)}`, {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        })
        .then(response => response.json())
        .then(data => {
            console.log('Fetched user data:', data);
            const usersContainer = document.querySelector('.custom-background-user ul');
            usersContainer.innerHTML = '';

            data.forEach(user => {
                const userElement = document.createElement('li');
                userElement.className = 'list-group-item';
                userElement.innerHTML = `<p>${user}</p>`;
                usersContainer.appendChild(userElement);
            });

        })
        .catch(error => console.error('Error fetching user data:', error));
    } else {
        console.error('No auth token found');
    }
}


document.addEventListener('DOMContentLoaded', function () {
    const messageForm = document.querySelector('#messageForm');
    messageForm.addEventListener('submit', function (event) {
        event.preventDefault();

        const token = localStorage.getItem('authToken');
        chatName = document.getElementById('chat-name').value
        if (!chatName) {
            const modal = new bootstrap.Modal(document.getElementById('messageModal'));
            modal.show();
            return;
        }

        if (token) {
            const messageChatData = {
                    user: document.getElementById('message-user').value,
                    message: document.getElementById('message-message').value,
                    chatName: document.getElementById('chat-name').value
            };

            //console.log('Message data:', messageChatData);
            fetch('/chat/api/chatAdd', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + token
                },
                body: JSON.stringify(messageChatData)
            })
            .then(response => {
                if (response.ok) {
                //console.log('Message saved successfully');
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
            fetch('/chat/api/chatCreate', {
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
                fetchUsers();
                //fetchUser();
            })
            .catch((error) => {
                console.error('Error:', error);
            });
        } else {
            console.error('No auth token found');
        }
    });
});


function loadProfile() {
    const panel = document.getElementById('panel');
    const token = localStorage.getItem('authToken'); // Retrieve the Bearer token from localStorage

    if (!panel) {
        console.error('Element with id "panel" not found.');
        return;
    }

    if (!token) {
        console.error('No Bearer token found in localStorage');
        // Optionally redirect to login
        return;
    }

    fetch('/chat/profile', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}` // Add the Bearer token to the Authorization header
        }
    })
    .then(response => {
        if (response.ok) {
            return response.text(); // Parse the response as text (HTML)
        } else {
            throw new Error(`Failed to load profile.html: ${response.status}`);
        }
    })
    .then(html => {
        panel.innerHTML = html; // Inject the HTML content into the #panel element
    })
    .catch(error => {
        console.error('Error loading profile.html:', error);
    });
}

function createNavbar() {
    // Create the main nav element
    const nav = document.createElement('nav');
    nav.className = 'navbar navbar-expand-lg bg-body-tertiary';

    // Create the container div
    const container = document.createElement('div');
    container.className = 'container-fluid';

    // Create the brand link
    const brand = document.createElement('a');
    brand.className = 'navbar-brand';
    brand.href = '#';
    brand.textContent = 'Navbar';

    // Create the toggle button
    const toggleButton = document.createElement('button');
    toggleButton.className = 'navbar-toggler';
    toggleButton.type = 'button';
    toggleButton.setAttribute('data-bs-toggle', 'collapse');
    toggleButton.setAttribute('data-bs-target', '#navbarSupportedContent');
    toggleButton.setAttribute('aria-controls', 'navbarSupportedContent');
    toggleButton.setAttribute('aria-expanded', 'false');
    toggleButton.setAttribute('aria-label', 'Toggle navigation');

    const toggleIcon = document.createElement('span');
    toggleIcon.className = 'navbar-toggler-icon';
    toggleButton.appendChild(toggleIcon);

    // Create the collapsible div
    const collapseDiv = document.createElement('div');
    collapseDiv.className = 'collapse navbar-collapse';
    collapseDiv.id = 'navbarSupportedContent';

    // Create the nav list
    const navList = document.createElement('ul');
    navList.className = 'navbar-nav me-auto mb-2 mb-lg-0';

    // Add nav items
    const navItems = [
        { text: 'Home', href: '#', className: 'nav-link active', ariaCurrent: 'page' },
        { text: 'Logout', href: 'logout', className: 'nav-link' },
    ];

    navItems.forEach(item => {
        const li = document.createElement('li');
        li.className = 'nav-item';

        const a = document.createElement('a');
        a.className = item.className;
        a.href = item.href;
        a.textContent = item.text;

        if (item.ariaCurrent) {
            a.setAttribute('aria-current', item.ariaCurrent);
        }

        li.appendChild(a);
        navList.appendChild(li);
    });

    // Add dropdown
    const dropdown = document.createElement('li');
    dropdown.className = 'nav-item dropdown';

    const dropdownToggle = document.createElement('a');
    dropdownToggle.className = 'nav-link dropdown-toggle';
    dropdownToggle.href = '#';
    dropdownToggle.role = 'button';
    dropdownToggle.setAttribute('data-bs-toggle', 'dropdown');
    dropdownToggle.setAttribute('aria-expanded', 'false');
    dropdownToggle.textContent = 'Dropdown';

    const dropdownMenu = document.createElement('ul');
    dropdownMenu.className = 'dropdown-menu';

    const dropdownItems = [
        { text: 'Profile', href: '#', id: 'profile-link', className: 'dropdown-item' },
        { text: 'Search User', href: '#', className: 'dropdown-item' },
        { isDivider: true },
        { text: 'Search Chat', href: '#', className: 'dropdown-item' },
    ];

    dropdownItems.forEach(item => {
        if (item.isDivider) {
            const divider = document.createElement('hr');
            divider.className = 'dropdown-divider';
            dropdownMenu.appendChild(divider);
        } else {
            const li = document.createElement('li');
            const a = document.createElement('a');
            a.className = item.className;
            a.href = item.href;
            a.textContent = item.text;

            if (item.id) {
                a.id = item.id;
            }

            li.appendChild(a);
            dropdownMenu.appendChild(li);
        }
    });

    dropdown.appendChild(dropdownToggle);
    dropdown.appendChild(dropdownMenu);
    navList.appendChild(dropdown);

    // Add disabled link
    const disabledItem = document.createElement('li');
    disabledItem.className = 'nav-item';

    const disabledLink = document.createElement('a');
    disabledLink.className = 'nav-link disabled';
    disabledLink.href = '#';
    disabledLink.setAttribute('aria-disabled', 'true');
    disabledLink.textContent = 'Disabled';

    disabledItem.appendChild(disabledLink);
    navList.appendChild(disabledItem);

    // Create the search form
    const form = document.createElement('form');
    form.className = 'd-flex';
    form.role = 'search';

    const input = document.createElement('input');
    input.className = 'form-control me-2';
    input.type = 'search';
    input.placeholder = 'Search';
    input.setAttribute('aria-label', 'Search');

    const button = document.createElement('button');
    button.className = 'btn btn-outline-success';
    button.type = 'submit';
    button.textContent = 'Search';

    form.appendChild(input);
    form.appendChild(button);

    // Assemble the navbar
    collapseDiv.appendChild(navList);
    collapseDiv.appendChild(form);
    container.appendChild(brand);
    container.appendChild(toggleButton);
    container.appendChild(collapseDiv);
    nav.appendChild(container);

    // Append to the body or a specific container
    document.body.appendChild(nav);
}


document.addEventListener('DOMContentLoaded', function () {
    const profileLink = document.getElementById('profile-link');

    if (profileLink) {
        profileLink.addEventListener('click', handleProfileLinkClick);
    } else {
        console.error('Element with id "profile-link" not found in the DOM.');
    }
});

document.addEventListener('click', function (event) {
    console.log("Event target:", event.target);

    // Check if the clicked element or its parent has the id 'edit-profile'
    const editProfileElement = event.target.closest('#edit-profile');
    if (editProfileElement) {
        event.preventDefault(); // Prevent default behavior
        console.log('Edit profile button clicked');
        clearProfileContent();
        fetchCard();
        // Add your logic here, e.g., navigate to another page
        // window.location.href = '/card';
    }
});

function handleProfileLinkClick(event) {
    event.preventDefault(); // Prevent default navigation
    console.log("Profile link clicked");
    clearPanel();
    loadProfile();
    // Add your logic here, e.g., fetching profile data or navigating
}


function clearPanel() {
    const panel = document.getElementById('panel');
    if (panel) {
        panel.innerHTML = ''; // Clear all existing content in the panel
    } else {
        console.error('Element with id "panel" not found.');
    }
}

function clearProfileContent() {
    const content = document.getElementById('profile-content');
    if (content) {
        content.innerHTML = ''; // Clear all existing content in the profile section
    } else {
        console.error('Element with id "profile-content" not found.');
    }
}


async function fetchCard() {
    const token = localStorage.getItem('authToken'); // Retrieve the auth token from local storage

    if (!token) {
        console.error('No auth token found');
        return;
    }

    try {
        const response = await fetch('/chat/card', {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const cardData = await response.text(); // Assuming the response is HTML content
        document.getElementById('profile-content').innerHTML = cardData; // Render the card content
        console.log('Card data fetched successfully');
    } catch (error) {
        console.error('Error fetching card data:', error);
    }
}


window.onload = function() {
    //loadUsername();
    //fetchUsers();
    fetchUser();
    scrollToBottom();
    //loadNavPanel();
    //setInterval(fetchMessages, 5000);
    setInterval(fetchChatMessages, 5000);
}