/* jshint esversion: 6 */

/**
 * AJAX call management
 */
function makeCall(method, url, formElement, cback, reset = true) {
	var req = new XMLHttpRequest(); // visible by closure
	req.onreadystatechange = function() {
		cback(req);
	}; // closure
	req.open(method, url);
	if (formElement == null) {
		req.send();
	} else {
		req.send(new FormData(formElement));
	}
	if (formElement !== null && reset === true) {
		formElement.reset();
	}
}

/**
 * Checks the validity of the given email address
 */
function validateEmail(mail) {
	var regex = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
	
	return regex.test(mail);
}
