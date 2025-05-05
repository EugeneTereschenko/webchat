async function checkAuth(email, password) {
    if (!email || !password) {
        console.error('Email and password are required');
        return { success: false, message: 'Email and password are required' };
    }

    const user = {
        email: email,
        password: password
    };

    try {
        const response = await fetch('/chat/api/check-auth', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(user)
        });

        if (!response.ok) {
            const errorData = await response.json();
            console.error('Error:', errorData.message);
            return { success: false, message: errorData.message };
        }

        const data = await response.json();
        console.log('Success:', data.message);
        console.log('Two-factor required:', data.twofactor);
        return { success: true, data };
    } catch (error) {
        console.error('Request failed:', error);
        return { success: false, message: 'Request failed' };
    }
}

async function handleLoginSuccess(data) {
    console.log('User logged in successfully', data);
    localStorage.setItem('authToken', data.token); // Store the token

    const token = localStorage.getItem('authToken');
    if (!token) {
        console.error('No auth token found');
        return;
    }

    try {
        const response = await fetch('/chat', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error('Failed to load resource: ' + response.status);
        }

        const htmlData = await response.text(); // Expecting HTML response
        document.open();
        document.write(htmlData);
        document.close();
    } catch (error) {
        console.error('Error loading resource', error);
    }
}

async function sendLoginFormData(email, password) {
    if (!email || !password) {
        alert('Please fill in both email and password');
        return { success: false, message: 'Email and password are required' };
    }

    const user = {
        email: email,
        password: password
    };

    try {
        const response = await fetch('/chat/api/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(user)
        });

        const data = await response.json();

        if (response.ok && data.success) {
            return { success: true, data };
        } else {
            return { success: false, message: data.message || 'Login failed' };
        }
    } catch (error) {
        console.error('Error logging in:', error);
        return { success: false, message: 'Request failed' };
    }
}


async function sendLoginFormDataOtp(email, password, userCode) {
    if (!email || !password) {
        alert('Please fill in both email and password');
        return { success: false, message: 'Email and password are required' };
    }

    const user = {
        email: email,
        password: password,
        userCode: userCode
    };

    try {
        const response = await fetch('/chat/api/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(user)
        });

        const data = await response.json();

        if (response.ok && data.success) {
            return { success: true, data };
        } else {
            return { success: false, message: data.message || 'Login failed' };
        }
    } catch (error) {
        console.error('Error logging in:', error);
        return { success: false, message: 'Request failed' };
    }
}



async function getTwoFactorsForm() {

    try {
        const response = await fetch('/chat/twofactor', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error('Failed to load two-factor form: ' + response.status);
        }

        const htmlData = await response.text(); // Expecting HTML response
        document.open();
        document.write(htmlData);
        document.close();
        return { success: true };
    } catch (error) {
        console.error('Error loading two-factor form:', error);
        return { success: false, message: 'Request failed' };
    }
}
/*
document.addEventListener('DOMContentLoaded', function () {
    const form = document.querySelector('#loginForm');
    form.addEventListener('submit', function (event) {
        event.preventDefault();

        const email = document.querySelector('input[id="typeEmailX"]').value;
        const password = document.querySelector('input[id="typePasswordX"]').value;
        checkAuth();
        sendLoginFormData(email, password)
            .then(response => {
                if (response.success) {
                    handleLoginSuccess(response.data);
                } else {
                    checkAuth()
                        .then(authResponse => {
                            if (authResponse.success) {
                                getTwoFactorsForm();
                            } else {
                                alert(authResponse.message);
                            }
                        });
                    //alert(response.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('An error occurred. Please try again.');
            });
    });
});
*/

function getTwoFactorCode() {
    let code = '';
    for (let i = 1; i <= 6; i++) {
        const digit = document.getElementById(`digit${i}`).value;
        if (!digit) {
            alert(`Please fill in all digits.`);
            return null;
        }
        code += digit;
    }
    return code;
}

// Example usage:
/*
const twoFactorCode = getTwoFactorCode();
if (twoFactorCode) {
    console.log('Two-factor code:', twoFactorCode);
}*/

document.addEventListener('click', function (event) {
    console.log("Event target:", event.target);

    user = {
        email: document.querySelector('input[id="typeEmailX"]').value,
        password: document.querySelector('input[id="typePasswordX"]').value
    }

    const loginButtonElement = event.target.closest('#loginButton');
    if (loginButtonElement) {
        event.preventDefault();
        console.log("Login form submitted");
        const email = document.querySelector('input[id="typeEmailX"]').value;
        const password = document.querySelector('input[id="typePasswordX"]').value;

        checkAuth(user.email, user.password)
            .then(authResponse => {
            console.log("Auth response:", authResponse);
                if (authResponse.twofactor) {
                    console.log("load two factor form");
                    getTwoFactorsForm();
                    const userCode = getTwoFactorCode();
                    sendLoginFormDataOtp(email, password, userCode)
                        .then(response => {
                            if (response.success) {
                                handleLoginSuccess(response.data);
                            } else {
                                alert(response.message);
                            }
                        })
                        .catch(error => {
                            console.error('Error:', error);
                            alert('An error occurred. Please try again.');
                        });
                } else {
                    sendLoginFormData(email, password)
                        .then(response => {
                        if (response.success) {
                            handleLoginSuccess(response.data);
                        } else {
                            alert(response.message);
                            }
                        })
                        .catch(error => {
                            console.error('Error:', error);
                            alert('An error occurred. Please try again.');
                            });
                        }
            });
        }


        const twoFactorButtonElement = event.target.closest('#twoFactorButton');
        if (twoFactorButtonElement) {

        }

});