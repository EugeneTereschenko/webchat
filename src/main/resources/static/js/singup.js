document.addEventListener('DOMContentLoaded', function () {
    const form = document.querySelector('#signupForm');
    form.addEventListener('submit', function (event) {
        event.preventDefault();

        const name = document.querySelector('input[name="name"]').value;
        const email = document.querySelector('input[name="email"]').value;
        const password = document.querySelector('input[name="password"]').value;
        const repeatPassword = document.querySelector('input[name="repeatPassword"]').value;
        const agreeToTerms = document.querySelector('input[name="agreeToTerms"]').checked;

        if (password !== repeatPassword) {
            alert('Passwords do not match');
            return;
        }

        if (!agreeToTerms) {
            alert('You must agree to the terms of service');
            return;
        }

        const user = {
            username: name,
            email: email,
            password: password
        };

        fetch('/chat/api/singup', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(user)
        })
        .then(response => response.json())
        .then(data => {
           // console.log('User registered successfully', data);
            window.location.href = 'login';
        })
        .catch(error => {
            console.error('Error registering user', error);
        });
    });
});