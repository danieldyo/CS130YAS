function logData() {
	fetch("/api/proxy")
	.then(response => {
		response.json().then(data => {
			console.log(data);
			//alert("See console for logged object!")
		});
	});
}

function productSearch() {
    $('#productSearch').attr('action','/api/search');
}

function productSearchAsc() {
    $('#productSearch').attr('action','/api/searchAsc');
}

function productSearchDesc() {
    $('#productSearch').attr('action','/api/searchDesc');
}

/*
function upcSearch() {
    $('#productSearch').attr('action','/api/searchUPC');
}
*/
