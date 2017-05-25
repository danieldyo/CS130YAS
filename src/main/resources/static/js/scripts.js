function logData() {
	fetch("/api/proxy")
	.then(response => {
		response.json().then(data => {
			console.log(data);
			//alert("See console for logged object!")
		});
	});
}

function productSearchInitial() {
    $('#productSearch').attr('action','/api/searchInitial');
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

function productNextPage() {
    $('#productSearch').attr('action','/api/nextPage');
}

function productPrevPage() {
    $('#productSearch').attr('action','/api/prevPage');
}