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


function loadProfileData() {
    const token = localStorage.getItem('authToken'); // Retrieve the Bearer token from localStorage

    if (!token) {
        console.error('No Bearer token found in localStorage');
        return;
    }

    fetch('/chat/api/profile', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}` // Add the Bearer token to the Authorization header
        }
    })
    .then(response => {
        if (response.ok) {
            return response.json(); // Parse the response as JSON
        } else {
            throw new Error(`Failed to load profile data: ${response.status}`);
        }
    })
    .then(data => {
        console.log('Profile data:', data);
            const userNameElement = document.getElementById('profile-user-name');
            const firstNameElement = document.getElementById('profile-firstName');
            const lastNameElement = document.getElementById('profile-lastName');
            const emailElement = document.getElementById('profile-email');
            const phoneNumberElement = document.getElementById('profile-phone');
            const bioElement = document.getElementById('profile-bio');
            const staffElement = document.getElementById('profile-product-id');

            if (userNameElement) userNameElement.textContent = data.username;
            if (firstNameElement) firstNameElement.value = data.firstName;
            if (lastNameElement) lastNameElement.value = data.lastName;
            if (emailElement) emailElement.value = data.email;
            if (phoneNumberElement) phoneNumberElement.value = data.phoneNumber;
            if (bioElement) bioElement.value = data.bio;
            if (staffElement) staffElement.value = data.staff;
    })
    .catch(error => {
        console.error('Error loading profile data:', error);
    });
}

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
        getImage();
    })
    .catch(error => {
        console.error('Error loading profile.html:', error);
    });
}

async function loadBilling() {
    const token = localStorage.getItem('authToken'); // Retrieve the Bearer token from localStorage
    if (!token) {
        console.error('No Bearer token found in localStorage');
        return;
    }

    try {
        const response = await fetch('/chat/billing', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}` // Add the Bearer token to the Authorization header
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to load billing.html: ${response.status}`);
        }

        const billingData = await response.text(); // Parse the response as HTML content
        document.getElementById('profile-content').innerHTML = billingData; // Render the card content
        console.log('Billing data fetched successfully');
        loadBillingData();
    } catch (error) {
        console.error('Error fetching billing data:', error);
    }
}

async function loadBillingData() {
    const token = localStorage.getItem('authToken'); // Retrieve the Bearer token from localStorage

    if (!token) {
        console.error('No Bearer token found in localStorage');
        return;
    }

    try {
        const response = await fetch('api/allProfiles', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}` // Add the Bearer token to the Authorization header
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to fetch profiles: ${response.status}`);
        }

        const profiles = await response.json(); // Parse the response as JSON
        const container = document.getElementById('formControlLg'); // Target container for billing data

        if (container) {
            container.innerHTML = ''; // Clear existing content

            profiles.forEach(profile => {
                const profileHtml = `
                    <div class="mt-4 d-flex justify-content-between align-items-center">
                        <div class="d-flex flex-row align-items-center">
                            <img src="https://i.imgur.com/qHX7vY1.webp" class="rounded" width="70" />
                            <div class="d-flex flex-column ms-3">
                                <span class="h5 mb-1">${profile.cardType || 'Credit Card'}</span>
                                <span class="small text-muted">${profile.cardNumber || 'XXXX XXXX XXXX XXXX'}</span>
                            </div>
                        </div>
                        <div>
                            <input type="text" class="form-control" placeholder="CVC" style="width: 70px;" />
                        </div>
                    </div>
                `;
                container.insertAdjacentHTML('beforeend', profileHtml); // Append the profile HTML
            });
        } else {
            console.error('Element with id "formControlLg" not found.');
        }
    } catch (error) {
        console.error('Error fetching billing data:', error);
    }
}


function getImage() {
    const token = localStorage.getItem('authToken'); // Retrieve the Bearer token from localStorage
/*    const profilePicture = document.getElementById('profile-picture');

    if (profilePicture) {
        profilePicture.class = 'https://randomuser.me/api/portraits/men/40.jpg';
    }*/

    if (!token) {
        console.error('No Bearer token found in localStorage');
        return;
    }

    fetch('/chat/getImage', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}` // Add the Bearer token to the Authorization header
        }
    })
    .then(response => {
        loadProfileData();
        if (!response.ok) {
            throw new Error(`Failed to fetch image: ${response.status}`);
        }
        return response.blob(); // Parse the response as a blob
    })
    .then(blob => {
        const profilePicture = document.getElementById('profile-picture');
        if (profilePicture) {
            profilePicture.src = URL.createObjectURL(blob);// Set the image source to the blob URL
        } else {
            console.error('Element with id "profile-picture" not found.');
        }
    })
    .catch(error => {
        console.error('Error fetching image:', error);
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
        { text: 'Home', href: '#', id: 'home-button', className: 'nav-link active', ariaCurrent: 'page' },
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

        if(item.id) {
            a.id = item.id;
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

document.addEventListener('click', function (event) {
    console.log("Event target:", event.target);

    const editProfileElement = event.target.closest('#edit-profile');
    if (editProfileElement) {
        event.preventDefault();
        clearProfileContent();
        fetchCard();
    }
    const chatCreate = event.target.closest('#button-custom-chat-create');
    if (chatCreate) {
        event.preventDefault();
        createChat();
        fetchUsers();
        fetchUser();
    }
    const messageAdd = event.target.closest('#button_send_message');
    if (messageAdd) {
        event.preventDefault();
        addMessageToChat();
    }

    const profileLink = event.target.closest('#profile-link');
    if (profileLink) {
        event.preventDefault();
        clearPanel();
        loadProfile();
        //loadProfileData();
    }

    const homeButton = event.target.closest('#home-button');
    if (homeButton) {
        event.preventDefault();
        clearPanel();
        getChat();
    }
    const profileInfo = event.target.closest('#profile-info');
    if (profileInfo) {
        event.preventDefault();
        clearPanel();
        loadProfile();
        //loadProfileData();
    }
    const checkoutFormButton = event.target.closest('#checkoutFormButton');
    if (checkoutFormButton) {
        event.preventDefault();
        sendProfile();
    }
    const checkoutBioFormButton = event.target.closest('#checkoutBioFormButton');
    if (checkoutBioFormButton) {
        event.preventDefault();
        sendBioInfo();
    }
    const changePhoto = event.target.closest('#change-pic');
    if (changePhoto) {
        event.preventDefault();
        addPhoto();
        clearPanel();
        loadProfile();
        //loadProfileData();
    }
    const billingInfo = event.target.closest('#profile-billing');
    if (billingInfo) {
        event.preventDefault();
        clearProfileContent()
        loadBilling();
        //loadBillingData();
    }
    const editBioElement = event.target.closest('#profile-edit-view');
    if (editBioElement) {
        event.preventDefault();
        clearProfileContent();
        loadBio();
    }
});

async function sendBioInfo() {
    const token = localStorage.getItem('authToken'); // Retrieve the Bearer token from localStorage

    if (!token) {
        console.error('No Bearer token found in localStorage');
        return;
    }

    const bioData = {
        staff: document.getElementById('profile-product-id').value,
        bio: document.getElementById('profile-bio').value,
    };

    try {
        const response = await fetch('/chat/profile', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(bioData)
        });

        if (!response.ok) {
            throw new Error(`Failed to update profile: ${response.status}`);
        }

        const result = await response.json();
        console.log('Profile updated successfully:', result);
    } catch (error) {
        console.error('Error updating profile:', error);
    }
}

async function loadBio() {
    const token = localStorage.getItem('authToken'); // Retrieve the Bearer token from localStorage

    if (!token) {
        console.error('No Bearer token found in localStorage');
        return;
    }

    try {
        const response = await fetch('/chat/bio', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}` // Add the Bearer token to the Authorization header
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to fetch bio: ${response.status}`);
        }

        const bioData = await response.text(); // Parse the response as HTML content
        const content = document.getElementById('profile-content'); // Target the container

        if (content) {
            content.innerHTML = bioData; // Render the bio content
        } else {
            console.error('Element with id "profile-content" not found.');
        }
    } catch (error) {
        console.error('Error fetching bio data:', error);
    }
}

async function updateProfile() {
    const token = localStorage.getItem('authToken'); // Retrieve the Bearer token from localStorage

    if (!token) {
        console.error('No Bearer token found in localStorage');
        return;
    }

    try {
        const response = await fetch('/chat/api/profile', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}` // Add the Bearer token to the Authorization header
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to load profile data: ${response.status}`);
        }

        const data = await response.json(); // Parse the response as JSON
        console.log('Profile data:', data);
    } catch (error) {
        console.error('Error loading profile data:', error);
    }
}

function addPhoto() {
    const token = localStorage.getItem('authToken');
    const modal = new bootstrap.Modal(document.getElementById('imageModal'));
    modal.show();


    const fileInput = document.getElementById('file-input');
    fileInput.addEventListener('change', function () {
        const file = fileInput.files[0];

        if (token && file) {
            const formData = new FormData();
            formData.append('file', file);

            fetch('/chat/upload', {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + token
                },
                body: formData
            })
            .then(response => {
                if (response.ok) {
                    console.log('File uploaded successfully');
                    //loadProfile(); // Optionally refresh the profile or update the UI
                } else {
                    throw new Error('Failed to upload file: ' + response.status);
                }
            })
            .catch(error => {
                console.error('Error uploading file', error);
            });
        } else {
            console.error('No auth token found or no file selected');
        }
    }, { once: true }); // Ensure the event listener is added only once
}


function addMessageToChat() {
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
}

function createChat(){
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
                setInterval(fetchChatMessages, 5000);
            })
            .catch((error) => {
                console.error('Error:', error);
            });
        } else {
            console.error('No auth token found');
        }
}

function sendProfile() {

    let cardType = '';
    let debtCard = document.getElementById('checkoutForm3');
    let type2Card = document.getElementById('checkoutForm4');
    let type3Card = document.getElementById('checkoutForm5');

    if (debtCard && debtCard.checked) {
        cardType = 'credit';
    }
    if (type2Card && type2Card.checked) {
        cardType = 'debt';
    }
    if (type3Card && type3Card.checked) {
        cardType = 'virtual';
    }

    console.log('Selected card type:', cardType);

    const profileData = {
        firstName: document.getElementById('form6Example1').value,
        lastName: document.getElementById('form6Example2').value,
        address: document.getElementById('form6Example4').value,
        phoneNumber: document.getElementById('form6Example6').value,
        cardType: cardType,
        nameOfCard: document.getElementById('formNameOnCard').value,
        cardNumber: document.getElementById('formCardNumber').value,
        cardExpiryDate: document.getElementById('formExpiration').value,
        cvv: document.getElementById('formCVV').value,
    }

    console.log('Profile data:', profileData);

    const token = localStorage.getItem('authToken');

    fetch('/chat/profile', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
            body: JSON.stringify(profileData)
        })
        .then(response => {
        if (response.ok) {
                    //console.log('Message saved successfully');
                //return response.json();
        } else {
            throw new Error('Failed to save profile: ' + response.status);
        }
        })
        .then(data => {
        console.log('Profile saved successfully', data);
                // Optionally, refresh the messages or update the UI

        })
        .catch(error => {
            console.error('Error saving profile', error);
        });
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

async function getChat() {
    const token = localStorage.getItem('authToken'); // Retrieve the auth token from local storage

    if (!token) {
        console.error('No auth token found');
        return;
    }

    try {
        const response = await fetch('/chat/chat', {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const chatHtml = await response.text(); // Assuming the response is HTML content
        const panel = document.getElementById('panel');
        if (panel) {
            panel.innerHTML = chatHtml; // Update the panel with the chat content
            scrollToBottom();
        } else {
            console.error('Element with id "panel" not found.');
        }
    } catch (error) {
        console.error('Error fetching chat data:', error);
    }
}

window.onload = function() {
    getChat();
}