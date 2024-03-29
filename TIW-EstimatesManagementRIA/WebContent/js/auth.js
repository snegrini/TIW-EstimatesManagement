/**
 * Handles the user authentication and signup.
 */
/* jshint esversion: 6 */

 (function() { // avoid variables ending up in the global scope
    var loginform, signupform;

    window.addEventListener("load", () => {
        loginform = document.getElementById("loginform");
        signupform = document.getElementById("signupform");

        loginform.style.display = "inline";
        signupform.style.display = "none";
    }, false);

    document.getElementById("loginbutton").addEventListener('click', (e) => {
        var form = e.target.closest("form");
        
        if (form.checkValidity()) {
            makeCall("POST", 'CheckLogin', e.target.closest("form"),
                function(req) {
                    if (req.readyState == XMLHttpRequest.DONE) {
                        var message = req.responseText;
                        switch (req.status) {
                            case 200:
                                var jsonMsg = JSON.parse(message);
                                sessionStorage.setItem("username", jsonMsg.username);
                                window.location.href = jsonMsg.location;          
                                break;
                            case 400: // bad request
                                document.getElementById("alertmessage").textContent = message;
                                break;
                            case 401: // unauthorized
                                document.getElementById("alertmessage").textContent = message;
                                break;
                            case 500: // server error
                                document.getElementById("alertmessage").textContent = message;
                                break;
                        }
                    }
                }
            );
        } else {
            form.reportValidity();
        }
    });

    document.getElementById("signupanchor").addEventListener("click", (e) => {
        loginform.style.display = "none";
        signupform.style.display = "inline";
    }, false);

    document.getElementById("loginanchor").addEventListener("click", (e) => {
        loginform.style.display = "inline";
        signupform.style.display = "none";
    }, false);

    document.getElementById("signupbutton").addEventListener("click", (e) => {
        var form = e.target.closest("form");
        
        // Checking validity via HTML5 constraint and JS custom methods checkFields()
        if (form.checkValidity() && checkFields()) { 
            sendData();
        } else {
            form.reportValidity();
        }

        // Private function via closure
        function checkFields() {
            var password = document.getElementById('password');
            var confirmPassword = document.getElementById('confirm_password');
            var email = document.getElementById('email');

            if (validateEmail(email.value)) {
                if (password.value === confirmPassword.value) {
                    return true;
                } else {
                    document.getElementById("alertmessagesignup").textContent = "Passwords do not match!";
                    return false;
                }
            } else {
                document.getElementById("alertmessagesignup").textContent = "Invalid email address!";
                return false;
            }
        }

        // Private function via closure
        function sendData() {
            makeCall("POST", 'AddNewCustomer', form, function(req) {
                if (req.readyState == XMLHttpRequest.DONE) {
                    var message = req.responseText;
                    switch (req.status) {
                        case 200:
                            loginform.style.display = "inline";
                            signupform.style.display = "none";
                            document.getElementById("alertmessage").textContent = message;      
                            break;
                        case 400: // bad request
                            document.getElementById("alertmessagesignup").textContent = message;
                            break;
                        case 401: // unauthorized
                            document.getElementById("alertmessagesignup").textContent = message;
                            break;
                        case 500: // server error
                            document.getElementById("alertmessagesignup").textContent = message;
                            break;
                        case 502: // bad gateway
                            document.getElementById("alertmessagesignup").textContent = message;
                            break;
                    }
                }
            });
        }
     }, false);
    
})();