function logData() {
	fetch("/api/proxy")
	.then(response => {
		response.json().then(data => {
			console.log(data);
			alert("See console for logged object!")
		});
	});
}