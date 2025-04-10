document.addEventListener('DOMContentLoaded', function () {
    const form = document.querySelector('#loginForm');
    form.addEventListener('submit', function (event) {
        event.preventDefault();

        const email = document.querySelector('input[id="typeEmailX"]').value;
        const password = document.querySelector('input[id="typePasswordX"]').value;

        if (!email || !password) {
            alert('Please fill in both email and password');
            return;
        }

        const user = {
            email: email,
            password: password
        };

        fetch('/chat/api/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(user)
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                console.log('User logged in successfully', data);
                localStorage.setItem('authToken', data.token); // Store the token

                // Redirect to the page
                //window.location.href = 'http://localhost:8080/';
                token = localStorage.getItem('authToken');
                 if (token) {
                        fetch('/chat', {
                            method: 'GET',
                            headers: {
                                'Authorization': 'Bearer ' + token
                            }
                        })
                        .then(response => {
                            if (response.ok) {
                                return response.text(); // Expecting HTML response
                            } else {
                                throw new Error('Failed to load resource: ' + response.status);
                            }
                        })
                        .then(data => {
                            document.open();
                            document.write(data);
                            document.close();
                        })
                        .catch(error => {
                            console.error('Error loading resource', error);
                        });
                    } else {
                        console.error('No auth token found');
                    }

            } else {
                alert('Login failed: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Error logging in', error);
        });
    });
});