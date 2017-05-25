/*function logData() {
	fetch("/api/proxy")
	.then(response => {
		response.json().then(data => {
			console.log(data);
			//alert("See console for logged object!")
		});
	});
}*/

function productSearchInitial() {
    $('#productSearch').attr('action','/api/searchInitial');
}

function productSearch() {
    $("#sort").val("relevancy_search");
    $('#productSearch').attr('action','/api/search');
}

function productSearchAsc() {
    $("#sort").val("price_asc");
    $('#productSearch').attr('action','/api/search');
}

function productSearchDesc() {
    $("#sort").val("price_desc");
    $('#productSearch').attr('action','/api/search');
}

function productNextPage() {
    $('#productSearch').attr('action','/api/nextPage');
}

function productPrevPage() {
    $('#productSearch').attr('action','/api/prevPage');
}