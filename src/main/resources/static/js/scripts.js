let n = 1; // Current page number
//let currentPageUser = 1; // Initialize current page variable
//let currentPageMessage = 1; // Initialize current page variable
//let currentPageChat = 1; // Initialize current page variable


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
    const messagesContainer = document.querySelector('#messages-list');
    if (messagesContainer) {
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }
}

function clearPanel() {
    const panel = document.getElementById('panel');
    if (panel) {
        panel.innerHTML = ''; // Clear all existing content in the panel
    } else {
        console.error('Element with id "panel" not found.');
    }
}

function clearModal() {
    const panel = document.getElementById('modal-container');
    if (panel) {
        panel.innerHTML = ''; // Clear all existing content in the panel
    } else {
        console.error('Element with id "modal-container" not found.');
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

function createMessageElement(avatar, username, time, messageContent) {
    const messageElement = document.createElement('li');
    messageElement.className = 'd-flex justify-content-between mb-4';
    messageElement.innerHTML = `
        <img src="${avatar || 'https://mdbcdn.b-cdn.net/img/Photos/Avatars/avatar-6.webp'}"
             alt="avatar"
             class="rounded-circle d-flex align-self-start me-3 shadow-1-strong"
             width="60">
        <div class="card">
            <div class="card-header d-flex justify-content-between p-3">
                <p class="fw-bold mb-0">${username || 'Unknown User'}</p>
                <p class="text-muted small mb-0"><i class="far fa-clock"></i> ${time || 'Just now'}</p>
            </div>
            <div class="card-body">
                <p class="mb-0">${messageContent || 'No message available'}</p>
            </div>
        </div>
    `;
    return messageElement;
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

async function fetchAndDisplayOldMessages(chatName) {
    const token = localStorage.getItem('authToken'); // Retrieve the auth token

    if (!token) {
        console.error('No auth token found');
        return;
    }

    try {
        const response = await fetch(`/chat/api/oldMessages?chatName=${encodeURIComponent(chatName)}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}` // Include the token in the Authorization header
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to fetch messages: ${response.status}`);
        }

        const messages = await response.json();
        const messagesContainer = document.querySelector('#messages-list');
        console.log('Fetched messages:', messages);
        if (messagesContainer) {
            messages.forEach(message => {
                const messageElement = createMessageElement(
                    message.avatar,
                    message.username,
                    message.time,
                    message.message
                );
                messagesContainer.appendChild(messageElement);
            });
        } else {
            console.error('Messages container not found');
        }
    } catch (error) {
        console.error('Error fetching messages:', error);
    }
}

async function checkAuth() {
    const token = localStorage.getItem('authToken'); // Retrieve the token from localStorage

    if (!token) {
        console.error('No auth token found');
        return { success: false, message: 'No auth token found' };
    }

    try {
        const response = await fetch('/chat/api/check-auth', {
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

async function checkNewMessages() {
    const token = localStorage.getItem('authToken'); // Retrieve the auth token

    if (!token) {
        console.error('No auth token found');
        return;
    }

    try {
        const response = await fetch('/chat/api/newMessages', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}` // Include the token in the Authorization header
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to check new messages: ${response.status}`);
        }

        const data = await response.json();
        console.log('New messages:', data);
        if (data) {
            fetchChatMessages();
        } else {
            console.log('No new messages');
        }
    } catch (error) {
        console.error('Error checking new messages:', error);
    }
}

async function fetchChatMessages() {
    const token = localStorage.getItem('authToken'); // Retrieve the auth token
    var chatName = document.getElementById('chat-name')?.value;
    const chatNameFromChat = document.getElementById('chat-name-value')?.textContent;

    if (!token) {
        console.error('No auth token found');
        return;
    }

    if (chatNameFromChat) {
        chatName = chatNameFromChat; // Use the chat name from the chat element
    }

    if (!chatName) {
        console.error('Chat name is required');
        return;
    }

    try {
        const response = await fetch(`/chat/api/newMessages?chatName=${encodeURIComponent(chatName)}`, {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to fetch chat messages: ${response.status}`);
        }

        const data = await response.json();
        console.log('Fetched chat messages:', data);
        const messagesContainer = document.querySelector('#messages-list');
        if (messagesContainer) {
            //messagesContainer.innerHTML = '';
            data.forEach(message => {
                messagesContainer.appendChild(createMessageElement(message.avatar, message.username, message.time, message.message));
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

async function fetchUsers() {
    const token = localStorage.getItem('authToken');
    const chatName = document.getElementById('chat-name')?.value;

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

        const usersContainer = document.querySelector('#users-list');
        if (usersContainer) {
            //usersContainer.innerHTML = '';

            data.forEach(user => {
                const userElement = document.createElement('li');
                userElement.className = 'p-2 border-bottom bg-body-tertiary';
                userElement.innerHTML = `
                    <a href="#!" class="d-flex justify-content-between">
                        <div class="d-flex flex-row">
                            <img src="${user.avatar || 'https://mdbcdn.b-cdn.net/img/Photos/Avatars/avatar-8.webp'}"
                                 alt="avatar"
                                 class="rounded-circle d-flex align-self-center me-3 shadow-1-strong"
                                 width="60">
                            <div class="pt-1">
                                <p class="fw-bold mb-0">${user.username || 'Unknown User'}</p>
                                <p class="small text-muted">${user.message || 'No message available'}</p>
                            </div>
                        </div>
                        <div class="pt-1">
                            <p class="small text-muted mb-1">${user.time || 'Just now'}</p>
                            ${user.unreadCount ? `<span class="badge bg-danger float-end">${user.unreadCount}</span>` : ''}
                        </div>
                    </a>
                `;
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

async function loadSearchUser() {
    const token = localStorage.getItem('authToken'); // Retrieve the Bearer token from localStorage

    if (!token) {
        console.error('No Bearer token found in localStorage');
        return;
    }

    try {
        const response = await fetch('/chat/search/user', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}` // Add the Bearer token to the Authorization header
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to load search user: ${response.status}`);
        }

        const html = await response.text(); // Parse the response as text (HTML)
        const modalContainer = document.getElementById('modal-container'); // Ensure a modal container exists
        if (modalContainer) {
            modalContainer.innerHTML = html; // Inject the HTML content into the modal container
            const modal = new bootstrap.Modal(modalContainer.querySelector('.modal')); // Initialize the modal
            modal.show(); // Show the modal
        } else {
            console.error('Modal container not found.');
        }
    } catch (error) {
        console.error('Error loading search user:', error);
    }
}

async function loadSearchChat() {
    const token = localStorage.getItem('authToken'); // Retrieve the Bearer token from localStorage

    if (!token) {
        console.error('No Bearer token found in localStorage');
        return;
    }

    try {
        const response = await fetch('/chat/search/chat', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}` // Add the Bearer token to the Authorization header
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to load search chat: ${response.status}`);
        }

        const html = await response.text(); // Parse the response as text (HTML)
        const modalContainer = document.getElementById('modal-container'); // Ensure a modal container exists
        if (modalContainer) {
            modalContainer.innerHTML = html; // Inject the HTML content into the modal container
            const modal = new bootstrap.Modal(modalContainer.querySelector('.modal')); // Initialize the modal
            modal.show(); // Show the modal
        } else {
            console.error('Modal container not found.');
        }
    } catch (error) {
        console.error('Error loading search chat:', error);
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
        const response = await fetch('/chat/api/allProfiles', {
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
        const response = await fetch('/chat/api/change-password', {
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
    const chatName = document.getElementById('chat-name-value')?.textContent; // Use textContent to get the span value
    if (!chatName || chatName.trim() === '') {
        const modal = new bootstrap.Modal(document.getElementById('messageModal'));
        modal.show();
        return;
    }
    if (!token) {
        console.error('No auth token found');
        return;
    }

    const messageChatData = {
        user: "",
        message: document.getElementById('message-message').value,
        chatName: document.getElementById('chat-name')?.value
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

async function sendChatName(chatNameToOpen) {
    const token = localStorage.getItem('authToken');
    let chatName = document.getElementById('chat-name')?.value; // Changed to let

    if (chatNameToOpen) {
        chatName = chatNameToOpen; // Now reassignment works
    }

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
            const errorText = await response.text();
            console.error('Error creating chat:', errorText);
            return;
        }

        const data = await response.json();
        console.log('Chat created successfully:', data);
        document.getElementById('chat-name-value').textContent = chatName;
        fetchUsers();
        fetchAndDisplayOldMessages(chatName);
        setInterval(checkNewMessages, 5000);
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

async function searchUser(keyword, page) {
    console.log('Searching for keyword:', keyword); // Log the keyword
    const token = localStorage.getItem('authToken');

    if (!token) {
        console.error('No auth token found');
        return;
    }

    try {
        const response = await fetch(`/chat/api/search/users?keyword=${encodeURIComponent(keyword)}&page=${encodeURIComponent(page)}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to search user: ${response.status}`);
        }

        const data = await response.json();
        console.log('Search results:', data);

        // Get the panel element
        const panel = document.getElementById('search-user-results');
        if (!panel) {
            console.error('Panel element not found');
            return;
        }

        // Clear previous content
        panel.innerHTML = '';

        // Create a container div
        const containerDiv = document.createElement('div');
        containerDiv.className = 'd-flex justify-content-center align-items-center';
        containerDiv.style.height = '100%'; // Optional: Adjust height if needed
        containerDiv.style.marginTop = '30px'; // Add margin-top of 30px

        // Append the container div to the panel
        panel.appendChild(containerDiv);


        // Create a list group
        const listGroup = document.createElement('ul');
        listGroup.className = 'list-group';

        data.forEach(result => {
            const listItem = document.createElement('li');
            listItem.className = 'list-group-item';
            listItem.textContent = `Chat: ${result.user}`; // Fixed string

            // Add a click event listener
            listItem.addEventListener('click', () => {
                console.log(`Clicked on: Chat: ${result.user}`);
                openChatById(result);
            });

            listGroup.appendChild(listItem);
        });

        // Append the list to the panel
        containerDiv.appendChild(listGroup);
        //panel.appendChild(listGroup);

        const paginationDiv = document.createElement('div');
        paginationDiv.id = 'pagination-container-user';
        panel.appendChild(paginationDiv);

        const containerPagination = document.getElementById('pagination-container-user'); // Example container
        if (containerPagination) {
            createPagination(3, containerPagination, "user");
        } else {
            console.error('Container element not found.');
        }

    } catch (error) {
        console.error('Error searching chat:', error);
    }
}


async function searchChat(keyword, page) {
    console.log('Searching for keyword:', keyword); // Log the keyword
    const token = localStorage.getItem('authToken');

    if (!token) {
        console.error('No auth token found');
        return;
    }

    try {
        const response = await fetch(`/chat/api/search/chats?keyword=${encodeURIComponent(keyword)}&page=${encodeURIComponent(page)}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to search chat: ${response.status}`);
        }

        const data = await response.json();
        console.log('Search results:', data);

        // Get the panel element
        const panel = document.getElementById('search-chat-results');
        if (!panel) {
            console.error('Panel element not found');
            return;
        }

        // Clear previous content
        panel.innerHTML = '';

        // Create a container div
        const containerDiv = document.createElement('div');
        containerDiv.className = 'd-flex justify-content-center align-items-center';
        containerDiv.style.height = '100%'; // Optional: Adjust height if needed
        containerDiv.style.marginTop = '30px'; // Add margin-top of 30px

        // Append the container div to the panel
        panel.appendChild(containerDiv);


        // Create a list group
        const listGroup = document.createElement('ul');
        listGroup.className = 'list-group';

        data.forEach(result => {
            const listItem = document.createElement('li');
            listItem.className = 'list-group-item';
            listItem.textContent = `Chat: ${result.chatName}`; // Fixed string

            // Add a click event listener
            listItem.addEventListener('click', () => {
                console.log(`Clicked on: Chat: ${result.chatName}`);
                openChatById(result);
            });

            listGroup.appendChild(listItem);
        });

        // Append the list to the panel
        containerDiv.appendChild(listGroup);
        //panel.appendChild(listGroup);

        const paginationDiv = document.createElement('div');
        paginationDiv.id = 'pagination-container-chat';
        panel.appendChild(paginationDiv);

        const containerPagination = document.getElementById('pagination-container-chat'); // Example container
        if (containerPagination) {
            createPagination(3, containerPagination, "chat");
        } else {
            console.error('Container element not found.');
        }

    } catch (error) {
        console.error('Error searching chat:', error);
    }
}


async function searchMessageForChat(keyword, page) {
    console.log('Searching for keyword:', keyword); // Log the keyword
    const token = localStorage.getItem('authToken');

    if (!token) {
        console.error('No auth token found');
        return;
    }

    try {
        const response = await fetch(`/chat/api/search/messages?keyword=${encodeURIComponent(keyword)}&page=${encodeURIComponent(page)}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to search messages: ${response.status}`);
        }

        const data = await response.json();
        console.log('Search results:', data);

        // Get the panel element
        const panel = document.getElementById('panel');
        if (!panel) {
            console.error('Panel element not found');
            return;
        }

        // Clear previous content
        panel.innerHTML = '';

        // Create a container div
        const containerDiv = document.createElement('div');
        containerDiv.className = 'd-flex justify-content-center align-items-center';
        containerDiv.style.height = '100%'; // Optional: Adjust height if needed
        containerDiv.style.marginTop = '30px'; // Add margin-top of 30px

        // Append the container div to the panel
        panel.appendChild(containerDiv);

        // Create a list group
        const listGroup = document.createElement('ul');
        listGroup.className = 'list-group';

        // Populate the list with search results
        data.forEach(result => {
            const listItem = document.createElement('li');
            listItem.className = 'list-group-item';
            listItem.textContent = `Chat: ${result.chatName}, Message: ${result.message}, User: ${result.user}`;

            // Add a click event listener
            listItem.addEventListener('click', () => {
                console.log(`Clicked on: Chat: ${result.chatName}, Message: ${result.message}, User: ${result.user}`);
                openChatById(result);
            });

            listGroup.appendChild(listItem);
        });

        // Append the list to the panel
        containerDiv.appendChild(listGroup);

        // Create and append the pagination container
        const paginationDiv = document.createElement('div');
        paginationDiv.id = 'pagination-container';
        panel.appendChild(paginationDiv);

        const containerPagination = document.getElementById('pagination-container'); // Example container
        console.log(" containerPagination " + containerPagination);
        if (containerPagination) {
            createPagination(3, containerPagination, "message");
        } else {
            console.error('Container element not found.');
        }
    } catch (error) {
        console.error('Error searching messages:', error);
    }
}


function searchRequest(type, current) {
    console.log('Search request:', type);
    if (type == 'chat') {
    const searchChatWord = document.getElementById('search-chat-input')?.value;
    console.log('Search chat:', searchChatWord);
        if (searchChat) {
            searchChat(searchChatWord, current);
        }
    }

    if (type == 'user') {
    const searchUserWord = document.getElementById('search-user-input')?.value;
    console.log('Search user:', searchUserWord);
        if (searchUserWord) {
            searchUser(searchUserWord, current);
        }
    }

    if (type == 'message') {
        const searchMessageWord = document.getElementById('chat-message')?.value;
        console.log('Search message:', searchMessageWord);
        if (searchMessageWord) {
            searchMessageForChat(searchMessageWord, current);
        }
    }
}



function openChatById(result) {
    console.log('Opening chat by ID:', result.id);
    console.log('Opening chat by name:', result.chatName);
    clearPanel();
    getChat();
    sendChatName(result.chatName);
}

function createPagination(num, paginationElementId, type) {
    console.log("create pagination");
    const containerDiv = document.createElement('div');
    containerDiv.className = 'd-flex justify-content-center align-items-center';
    containerDiv.style.marginTop = '20px';

    const nav = document.createElement('nav');
    nav.setAttribute('aria-label', 'Page navigation example');

    const ul = document.createElement('ul');
    ul.className = 'pagination';

    // "Previous" button
    const prevLi = document.createElement('li');
    prevLi.className = 'page-item';
    const prevLink = document.createElement('a');
    prevLink.className = 'page-link';
    prevLink.href = '#';
    prevLink.textContent = 'Previous';
    prevLink.addEventListener('click', (event) => {
        event.preventDefault();
        if (n > 1) {
            n--; // Decrement page number
            console.log('Previous page clicked, current page:', n);
            searchRequest(type, n);
        }
    });
    prevLi.appendChild(prevLink);
    ul.appendChild(prevLi);



    // Page numbers
    for (let i = n; i < n + num; i++) {
        const li = document.createElement('li');
        li.className = 'page-item';
        const link = document.createElement('a');
        link.className = 'page-link';
        link.href = '#';

        if (type == 'chat') {
            link.textContent = i;
        }
        if (type == 'user') {
            link.textContent = i;
        }
        if (type == 'message') {
            link.textContent = i;
        }

        link.addEventListener('click', (event) => {
            event.preventDefault();
            n = i; // Set current page to clicked page
            console.log(`Page ${i} clicked`);
            searchRequest(type, i);
        });

        li.appendChild(link);
        ul.appendChild(li);
    }

    // "Next" button
    const nextLi = document.createElement('li');
    nextLi.className = 'page-item';
    const nextLink = document.createElement('a');
    nextLink.className = 'page-link';
    nextLink.href = '#';
    nextLink.textContent = 'Next';
    nextLink.addEventListener('click', (event) => {
        event.preventDefault();
        n++; // Increment page number
        console.log('Next page clicked, current page:', n);
        searchRequest(type, n);
    });
    nextLi.appendChild(nextLink);
    ul.appendChild(nextLi);

    nav.appendChild(ul);
    containerDiv.appendChild(nav);
    paginationElementId.appendChild(containerDiv);
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

        if (item.id) {
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
        { text: 'Search User', href: '#', id: 'search-user-link', className: 'dropdown-item' },
        { isDivider: true },
        { text: 'Search Chat', href: '#', id: 'search-chat-link', className: 'dropdown-item' },
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
    input.id = 'chat-message';

    const button = document.createElement('button');
    button.className = 'btn btn-outline-success';
    button.type = 'submit';
    button.textContent = 'Search';
    button.id = 'button-search-chat';

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
    const chatCreate = event.target.closest('#button-add-chat');
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
    const searchUserLink = event.target.closest('#search-user-link');
    if (searchUserLink) {
        event.preventDefault();
        //clearPanel();
        loadSearchUser();
    }
    const searchChatLink = event.target.closest('#search-chat-link');
    if (searchChatLink) {
        event.preventDefault();
        //clearPanel();
        loadSearchChat();
        //getChat();
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
    const searchMessageElement = event.target.closest('#button-search-chat');
    if (searchMessageElement) {
        event.preventDefault();
        n = 1;
        const searchMessage = document.getElementById('chat-message')?.value;
        console.log('Search message:', searchMessage);
        if (searchMessage) {
            clearPanel();
            searchMessageForChat(searchMessage, n);
        }
    }
    const searchChatElement = event.target.closest('#search-chat-button');
    if (searchChatElement) {
        event.preventDefault();
        n = 1;
        const searchChatWord = document.getElementById('search-chat-input')?.value;
        console.log('Search chat:', searchChatWord);
        if (searchChat) {
            searchChat(searchChatWord, n);
        }
    }
    const searchUserElement = event.target.closest('#search-user-button');
    if (searchUserElement) {
        event.preventDefault();
        n = 1;
        const searchUserWord = document.getElementById('search-user-input')?.value;
        console.log('Search user:', searchUserWord);
        if (searchUserWord) {
            searchUser(searchUserWord, n);
        }
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

window.onload = function () {
    getChat();
}