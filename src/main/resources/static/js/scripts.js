/*****************************************************************************************************
 * Your Awesome Stuff (YAS) - Web Application where buyers or sellers can find the price of a product.
 * Copyright (C) 2017 CS130YAS
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *****************************************************************************************************
 */

function productSearchInitial() {
    $('#productSearch').attr('action','/searchInitial');
}

function productSearch() {
    $("#sort").val("relevancy_search");
    $('#productSearch').attr('action','/search');
}

function productSearchAsc() {
    $("#sort").val("price_asc");
    $('#productSearch').attr('action','/search');
}

function productSearchDesc() {
    $('#productSearch').attr('action','/searchDesc')

    $('.desc').addClass('down');
    $("#sort").val("price_desc");
    $('#productSearch').attr('action','/search');
}

function productNextPage() {
    $('#productSearch').attr('action','/nextPage');
}

function productPrevPage() {
    $('#productSearch').attr('action','/prevPage');
}

function idSearch() {
    $('.productPage').attr('action','/searchId');
}

function deleteItem() {
    $('.productPage').attr('action','/profile');
}

function addItem() {
    var id = $('#item').val();
    $.ajax({
        url:"http://localhost:8080/addItem?id=" + id,
        success:function(data) {
            if (data == "no_user") {
                window.location.assign("http://localhost:8080/login");
            }
            else {
                $('#added').css('display', 'inline-block').fadeOut(2500);
            }
        },
        error: function () {
            console.log("failed to add item");
        }
    });
}

/*Based off the tutorial from https://www.w3schools.com/w3css/w3css_slideshow.asp*/
var slideIndex = 1;
showImgs(slideIndex);

function showImgs(n) {
    showImg(slideIndex += n);
}

function showImg(n) {
    var i;
    var x = document.getElementsByClassName("img-slides");
    if (n > x.length) {slideIndex = 1}
    if (n < 1) {slideIndex = x.length};
    for (i = 0; i < x.length; i++) {
        x[i].style.display = "none";
    }
    x[slideIndex-1].style.display = "block";
}

/*Based off the tutorial from https://www.w3schools.com/howto/howto_js_tabs.asp*/
//allow 'new' to show up on page load

$('#addItem').submit(function (event) {
    addItem();
    event.preventDefault();
});

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
