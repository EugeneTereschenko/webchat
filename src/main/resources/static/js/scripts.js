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

function setProfileElements() {
    const profileContent = document.getElementById('profile-content');
    profileContent.innerHTML = "<section style=\"background-color: #eee;\">" +
                                    "<div class=\"container py-5\">" +
                                        "<div class=\"row d-flex justify-content-center\">" +
                                            "<div class=\"col-md-12 col-lg-10 col-xl-8\">" +
                                                "<div id=\"log-info\"></div>" +
                                            "</div>" +
                                        "</div>" +
                                    "</div>" +
                                "</section>";
}

async function checkAuth() {
    const token = localStorage.getItem('authToken'); // Retrieve the token from localStorage

    if (!token) {
        console.error('No auth token found');
        return { success: false, message: 'No auth token found' };
    }

    try {
        const response = await fetch('/api/check-auth', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}` // Include the token in the Authorization header
            },
        });

        if (!response.ok) {
            const errorData = await response.json();
            console.error('Error:', errorData.message);
            return { success: false, message: errorData.message };
        }

        const data = await response.json();
        console.log('Success:', data.message);
        return { success: true, data };
    } catch (error) {
        console.error('Request failed:', error);
        return { success: false, message: 'Request failed' };
    }
}

async function twoFactors(twoFactorsEnable) {
    const token = localStorage.getItem('authToken'); // Retrieve the Bearer token from localStorage

    if (!token) {
        console.error('No Bearer token found in localStorage');
        return;
    }

    try {
        const response = await fetch(`/chat/api/twoFactors?twoFactors=${encodeURIComponent(twoFactorsEnable)}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}` // Add the Bearer token to the Authorization header
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to update two-factor authentication: ${response.status}`);
        }

        const data = await response.json(); // Parse the response as JSON
        console.log('Two-factor authentication updated successfully:', data);
    } catch (error) {
        console.error('Error updating two-factor authentication:', error);
    }
}

async function updateNotification(notification) {
    const token = localStorage.getItem('authToken'); // Retrieve the Bearer token from localStorage

    if (!token) {
        console.error('No Bearer token found in localStorage');
        return;
    }

    try {
        const response = await fetch(`/chat/updateNotification?notification=${notification}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}` // Add the Bearer token to the Authorization header
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to update notification: ${response.status}`);
        }

        const data = await response.json(); // Parse the response as JSON
        console.log('Notification updated successfully:', data);
    } catch (error) {
        console.error('Error updating notification:', error);
    }
}

async function fetchChatMessages() {
    const token = localStorage.getItem('authToken'); // Retrieve the auth token
    const chatName = document.getElementById('chat-name').value;

    if (!token) {
        console.error('No auth token found');
        return;
    }

    if (!chatName) {
        console.error('Chat name is required');
        return;
    }

    try {
        const response = await fetch(`/chat/api/chat?chatName=${encodeURIComponent(chatName)}`, {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to fetch chat messages: ${response.status}`);
        }

        const data = await response.json();
        const messagesContainer = document.querySelector('.custom-background-message ul');
        if (messagesContainer) {
            messagesContainer.innerHTML = '';
            data.forEach(message => {
                const messageElement = document.createElement('li');
                messageElement.className = 'list-group-item';
                messageElement.innerHTML = `<p>${message.user}</p><p>${message.message}</p>`;
                messagesContainer.appendChild(messageElement);
            });
            scrollToBottom();
        } else {
            console.error('Messages container not found');
        }

        console.log('Fetched chat messages:', data);
    } catch (error) {
        console.error('Error fetching chat messages:', error);
    }
}


async function fetchUser() {
    const token = localStorage.getItem('authToken');
    const chatName = document.getElementById('chat-name').value;

    if (!token) {
        console.error('No auth token found');
        return;
    }

    try {
        const response = await fetch(`/chat/api/user?chatName=${encodeURIComponent(chatName)}`, {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to fetch user data: ${response.status}`);
        }

        const data = await response.json();
        console.log('Fetched user data:', data);

        const userMessageElement = document.querySelector('#message-user');
        if (userMessageElement) {
            userMessageElement.value = data.username;
        }
    } catch (error) {
        console.error('Error fetching user data:', error);
    }
}

async function fetchUsers() {
    const token = localStorage.getItem('authToken');
    const chatName = document.getElementById('chat-name').value;

    if (!token) {
        console.error('No auth token found');
        return;
    }

    try {
        const response = await fetch(`/chat/api/users?chatName=${encodeURIComponent(chatName)}`, {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to fetch users: ${response.status}`);
        }

        const data = await response.json();
        console.log('Fetched user data:', data);

        const usersContainer = document.querySelector('.custom-background-user ul');
        if (usersContainer) {
            usersContainer.innerHTML = '';

            data.forEach(user => {
                const userElement = document.createElement('li');
                userElement.className = 'list-group-item';
                userElement.innerHTML = `<p>${user}</p>`;
                usersContainer.appendChild(userElement);
            });
        }
    } catch (error) {
        console.error('Error fetching user data:', error);
    }
}

async function loadProfileData() {
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

        const userNameElement = document.getElementById('profile-user-name');
        const firstNameElement = document.getElementById('profile-firstName');
        const lastNameElement = document.getElementById('profile-lastName');
        const emailElement = document.getElementById('profile-email');
        const phoneNumberElement = document.getElementById('profile-phone');
        const bioElement = document.getElementById('profile-bio');
        const staffElement = document.getElementById('profile-position');
        const isActive = document.getElementById('formUserLock');
        const notification = document.getElementById('formEmailNotification');
        const isAuthorize = document.getElementById('formTwoFactors');

        if (userNameElement) userNameElement.textContent = data.username;
        if (firstNameElement) firstNameElement.value = data.firstName;
        if (lastNameElement) lastNameElement.value = data.lastName;
        if (emailElement) emailElement.value = data.email;
        if (phoneNumberElement) phoneNumberElement.value = data.phoneNumber;
        if (bioElement) bioElement.value = data.bio;
        if (staffElement) staffElement.innerHTML = data.staff;
        if (isActive) {
            isActive.checked = data.isActive === 'true'; // Set checkbox based on `isActive` value
        }
        if (notification) {
            notification.checked = data.notification === 'true'; // Set checkbox based on `notification` value
        }
        if (isAuthorize) {
            isAuthorize.checked = data.twoFactors === 'true';
        }


        await loadProfileActivity("activity-list", 3);
    } catch (error) {
        console.error('Error loading profile data:', error);
    }
}

async function loadProfile() {
    const panel = document.getElementById('panel');
    const token = localStorage.getItem('authToken'); // Retrieve the Bearer token from localStorage

    if (!panel) {
        console.error('Element with id "panel" not found.');
        return;
    }

    if (!token) {
        console.error('No Bearer token found in localStorage');
        return;
    }

    try {
        const response = await fetch('/chat/profile', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}` // Add the Bearer token to the Authorization header
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to load profile.html: ${response.status}`);
        }

        const html = await response.text(); // Parse the response as text (HTML)
        panel.innerHTML = html; // Inject the HTML content into the #panel element
        await getImage(); // Load the profile image
    } catch (error) {
        console.error('Error loading profile.html:', error);
    }
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

async function getImage() {
    const token = localStorage.getItem('authToken'); // Retrieve the Bearer token from localStorage

    if (!token) {
        console.error('No Bearer token found in localStorage');
        return;
    }

    try {
        const response = await fetch('/chat/getImage', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}` // Add the Bearer token to the Authorization header
            }
        });

        loadProfileData();

        if (!response.ok) {
            throw new Error(`Failed to fetch image: ${response.status}`);
        }

        const blob = await response.blob(); // Parse the response as a blob
        const profilePicture = document.getElementById('profile-picture');
        if (profilePicture) {
            profilePicture.src = URL.createObjectURL(blob); // Set the image source to the blob URL
        } else {
            console.error('Element with id "profile-picture" not found.');
        }
    } catch (error) {
        console.error('Error fetching image:', error);
    }
}

async function changePasswordData() {
    const token = localStorage.getItem('authToken'); // Retrieve the Bearer token from localStorage

    if (!token) {
        console.error('No Bearer token found in localStorage');
        return;
    }

    const oldPassword = document.getElementById('currentPassword').value;
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (newPassword !== confirmPassword) {
        console.error('New password and confirmation do not match');
        return;
    }

    const passwordData = {
        password: oldPassword,
        newPassword: newPassword,
    };

    try {
        const response = await fetch('api/change-password', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(passwordData)
        });

        if (!response.ok) {
            throw new Error(`Failed to update password: ${response.status}`);
        }

        const result = await response.json();
        console.log('Password updated successfully:', result);
    } catch (error) {
        console.error('Error updating password:', error);
    }
}

async function lockUser() {
    console.log('User is locked set');
    const token = localStorage.getItem('authToken'); // Retrieve the auth token from localStorage

    if (!token) {
        console.error('No auth token found');
        return;
    }

    try {
        const response = await fetch('/chat/locked', {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        });

        if (!response.ok) {
            throw new Error('Failed to lock user: ' + response.status);
        }

        const data = await response.json();
        console.log('Response:', data);
    } catch (error) {
        console.error('Error:', error);
    }
}

async function userUnlock() {
    console.log('User is unlocked set');
    const token = localStorage.getItem('authToken'); // Retrieve the auth token from localStorage

    if (!token) {
        console.error('No auth token found');
        return;
    }

    try {
        const response = await fetch('/chat/unlocked', {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        });

        if (!response.ok) {
            throw new Error('Failed to unlock user: ' + response.status);
        }

        const data = await response.json();
        console.log('Response:', data);
    } catch (error) {
        console.error('Error:', error);
    }
}

async function sendBioInfo() {
    const token = localStorage.getItem('authToken'); // Retrieve the Bearer token from localStorage

    if (!token) {
        console.error('No Bearer token found in localStorage');
        return;
    }

    const bioData = {
        username: document.getElementById('profile-username').value,
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
        // get token and set it to local storage
        localStorage.setItem('authToken', result.token); // Assuming the response contains a new token
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

async function sendProfile() {
    let cardType = '';
    const debtCard = document.getElementById('checkoutForm3');
    const type2Card = document.getElementById('checkoutForm4');
    const type3Card = document.getElementById('checkoutForm5');

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
    };

    console.log('Profile data:', profileData);

    const token = localStorage.getItem('authToken');

    if (!token) {
        console.error('No auth token found');
        return;
    }

    try {
        const response = await fetch('/chat/profile', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify(profileData)
        });

        if (!response.ok) {
            throw new Error(`Failed to save profile: ${response.status}`);
        }

        const data = await response.json();
        console.log('Profile saved successfully', data);
        // Optionally, refresh the messages or update the UI
    } catch (error) {
        console.error('Error saving profile:', error);
    }
}

async function addPhoto() {
    const token = localStorage.getItem('authToken');
    const modal = new bootstrap.Modal(document.getElementById('imageModal'));
    modal.show();

    const fileInput = document.getElementById('file-input');
    fileInput.addEventListener('change', async function () {
        const file = fileInput.files[0];

        if (!token) {
            console.error('No auth token found');
            return;
        }

        if (!file) {
            console.error('No file selected');
            return;
        }

        const formData = new FormData();
        formData.append('file', file);

        try {
            const response = await fetch('/chat/upload', {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + token
                },
                body: formData
            });

            if (!response.ok) {
                throw new Error('Failed to upload file: ' + response.status);
            }

            console.log('File uploaded successfully');
            // Optionally refresh the profile or update the UI
            // loadProfile();
        } catch (error) {
            console.error('Error uploading file:', error);
        }
    }, { once: true }); // Ensure the event listener is added only once
}


async function sendChatMessage() {
        const token = localStorage.getItem('authToken');
        chatName = document.getElementById('chat-name').value
        if (!chatName) {
            const modal = new bootstrap.Modal(document.getElementById('messageModal'));
            modal.show();
            return;
        }
        if (!token) {
           console.error('No auth token found');
           return;
        }

        const messageChatData = {
            user: document.getElementById('message-user').value,
            message: document.getElementById('message-message').value,
            chatName: document.getElementById('chat-name').value
        };
        console.log('Message     data:', messageChatData);

        try {
            response = await fetch('/chat/api/chatAdd', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + token
                    },
                body: JSON.stringify(messageChatData)
            })
            if (response.ok) {
                console.log('Message saved successfully');
                fetchChatMessages();
            }
        } catch (error) {
            console.error('Failed to save message', error);
        }
}


async function loadProfileActivity(idElement, numOfElements) {
    const token = localStorage.getItem('authToken'); // Retrieve the Bearer token from localStorage

    if (!token) {
        console.error('No Bearer token found in localStorage');
        return;
    }

    try {
        const response = await fetch(`/chat/activity?numOfLogs=${encodeURIComponent(numOfElements)}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}` // Add the Bearer token to the Authorization header
            }
        });

        if (!response.ok) {
            throw new Error('Failed to fetch activity data');
        }

        const data = await response.json();
        const activityList = document.getElementById(idElement);
        activityList.innerHTML = ''; // Clear existing content

        // Iterate over the response data and create activity items
        for (const [activity, time] of Object.entries(data)) {
            if (activity === 'message' || activity === 'success') continue; // Skip metadata

            const activityItem = document.createElement('div');
            activityItem.className = 'activity-item mb-3';

            const activityTitle = document.createElement('h6');
            activityTitle.className = 'mb-1';
            activityTitle.textContent = activity;

            const activityTime = document.createElement('p');
            activityTime.className = 'text-muted small mb-0';
            activityTime.textContent = time;

            activityItem.appendChild(activityTitle);
            activityItem.appendChild(activityTime);
            activityList.appendChild(activityItem);
        }
    } catch (error) {
        console.error('Error loading profile activity:', error);
    }
}

async function sendChatName() {
    const token = localStorage.getItem('authToken');
    const chatName = document.getElementById('chat-name').value;

    if (!chatName) {
        console.error('Chat name is required');
        return;
    }

    if (!token) {
        console.error('No auth token found');
        return;
    }

    try {
        const response = await fetch('/chat/api/chatCreate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'Authorization': 'Bearer ' + token
            },
            body: new URLSearchParams({
                'name': chatName
            })
        });

        if (!response.ok) {
            const errorText = await response.text(); // Read the response as text
            console.error('Error creating chat:', errorText);
            return;
        }

        const data = await response.json();
        console.log('Chat created successfully:', data);
        document.getElementById('chat-name-value').textContent = chatName;
        fetchUsers();
        fetchUser();
        setInterval(fetchChatMessages, 5000);
    } catch (error) {
        console.error('Error creating chat:', error);
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
        //createChat();
        sendChatName();
    }
    const messageAdd = event.target.closest('#button_send_message');
    if (messageAdd) {
        event.preventDefault();
        //addMessageToChat();
        sendChatMessage();
    }

    const profileLink = event.target.closest('#profile-link');
    if (profileLink) {
        event.preventDefault();
        clearPanel();
        loadProfile();
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
    }
    const billingInfo = event.target.closest('#profile-billing');
    if (billingInfo) {
        event.preventDefault();
        clearProfileContent()
        loadBilling();
    }
    const editBioElement = event.target.closest('#profile-edit-view');
    if (editBioElement) {
        event.preventDefault();
        clearProfileContent();
        loadBio();
    }
    const changePassword = event.target.closest('#checkoutPasswordFormButton');
    if (changePassword) {
         event.preventDefault();
         changePasswordData();
    }
    const modalClosePictureElement = event.target.closest('#modalClosePicture');
    if (modalClosePictureElement) {
        event.preventDefault();
        getImage();
    }
    const profileActivityElement = event.target.closest('#profile-activity');
    if (profileActivityElement) {
        event.preventDefault();
        clearProfileContent();
        setProfileElements();
        loadProfileActivity("log-info", 5);
    }
});

document.addEventListener('change', function (event) {
    console.log("Event target:", event.target);

    const lockUserElement = event.target.closest('#formUserLock');
    if (lockUserElement) {
        if (lockUserElement.checked) {
            console.log('User is unlocked');
            userUnlock();
        } else {
            console.log('User is locked');
            lockUser();
        }
    }
    const formEmailNotificationElement = event.target.closest('#formEmailNotification');
    if (formEmailNotificationElement) {
        if (formEmailNotificationElement.checked) {
            console.log('Email notifications are enabled');
            updateNotification(true);
        } else {
            console.log('Email notifications are disabled');
            updateNotification(false);
        }
    }
    const formTwoFactorsElement = event.target.closest('#formTwoFactors');
    if (formTwoFactorsElement) {
        if (formTwoFactorsElement.checked) {
            console.log('TwoFactors are enabled');
            twoFactors(true);
        } else {
            console.log('TwoFactors are disabled');
            twoFactors(false);
        }
    }
});

window.onload = function() {
    getChat();
}