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
    $('#productSearch').attr('action','/api/searchDesc')

    $('.desc').addClass('down');
    $("#sort").val("price_desc");
    $('#productSearch').attr('action','/api/search');
}

function productNextPage() {
    $('#productSearch').attr('action','/api/nextPage');
}

function productPrevPage() {
    $('#productSearch').attr('action','/api/prevPage');
}

function idSearch() {
    $('.productPage').attr('action','/api/searchId');
}

function deleteItem() {
    $('.productPage').attr('action','/api/profile');
}

function addItem() {
    $('#addItem').attr('action','/api/searchId');
}

/*Based off the tutorial from https://www.w3schools.com/howto/howto_js_tabs.asp*/

function productTab(evt, condition) {
    // Declare all variables
    var i, tabcontent, tablinks;

    // Get all elements with class="tabcontent" and hide them
    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }

    // Get all elements with class="tablinks" and remove the class "active"
    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }

    // Show the current tab, and add an "active" class to the button that opened the tab
    document.getElementById(condition).style.display = "block";
    evt.currentTarget.className += " active";
}
