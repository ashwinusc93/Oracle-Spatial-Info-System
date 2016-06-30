var favorites = [];

// Load data from local storage
if (typeof(Storage) === "undefined") {
    console.error("Local Storage feature does not exist.");   
}

if (localStorage.getItem("favorites") !== null)
{
    favorites = JSON.parse(localStorage.getItem("favorites"));
    console.log(favorites);
}

function add_favorite(companyname)
{
    companyname = companyname.toUpperCase().trim();
    if (favorites.indexOf(companyname) < 0)
    {
        favorites.push(companyname);
        localStorage.setItem("favorites", JSON.stringify(favorites));
        update_favorites();
    }
}

function get_favorites()
{
    return favorites;
}

function load_fav_stock(companyname, rowId)
{
    $.ajax({
        url : "index.php?page=stock",
        type: "GET",
        data: {
            stock : companyname
        },
        dataType: 'JSON',
        success : function(data) {
            console.log(data);
            
            $("#" + rowId).empty();
            $("#" + rowId).append('<td><a href="#" id="favlink' + rowId + '">' + data.Symbol + '</a></td>');
            $("#" + rowId).append('<td>' + data.Name + '</td>');
            $("#" + rowId).append('<td>' + data.LastPrice.toFixed(2) + '</td>');
            if (data.ChangePercent < 0)
                $("#" + rowId).append("<td>" + data.Change.toFixed(2) + "(" + data.ChangePercent.toFixed(2) + "%)&nbsp;" + "<img src=\"http://cs-server.usc.edu:45678/hw/hw8/images/down.png\"></td>");
            else
                $("#" + rowId).append("<td>" + data.Change.toFixed(2) + "(" + data.ChangePercent.toFixed(2) + "%)&nbsp;" + "<img src=\"http://cs-server.usc.edu:45678/hw/hw8/images/up.png\"></td>");
            
            if (data.MarketCap<1000000)
                $("#" + rowId).append("<td>" + data.MarketCap + "</td>");
            else if (data.MarketCap>=1000000 && data.MarketCap<1000000000)
                $("#" + rowId).append("<td>" + (data.MarketCap/1000000).toFixed(2) + " Million" + "</td>");
            else
                $("#" + rowId).append("<td>" + (data.MarketCap/1000000000).toFixed(2) + " Billion" + "</td>");
            
            var trashcan = $('<button class="btn btn-default"><span class="glyphicon glyphicon-trash"></span></button>');
            trashcan.click(function() {
                delete_favorite(companyname);
            });
            $("#" + rowId).append('<td></td>');
            $("#" + rowId + " td:last").append(trashcan);

            $('#favlink' + rowId).click(function() {
                load_stock(data.Symbol);
                $("#stock-carousel").carousel(1);
            });
        }
    });
}

function update_favorites()
{
    if (localStorage.getItem("favorites") !== null)
        favorites = JSON.parse(localStorage.getItem("favorites"));
    else
        favorites = [];
    $('#favlist').empty();
    favorites.forEach(function(company, index) {
        var rowId = 'fav' + index;
        $('#favlist').append('<tr id="' + rowId + '"><td><a href="#" id="favlink' + index + '">' + company + '</a></td><td>Loading ...</td></tr>');
        load_fav_stock(company, rowId);
        $('#favlink' + index).click(function() {
            load_stock(company);
            $("#stock-carousel").carousel(1);
        });
    });
}

var autorefresh_enabled = false;
var timer = null;
function autorefresh_favorites()
{
    autorefresh_enabled = !autorefresh_enabled;
    if (autorefresh_enabled) {
        timer = setInterval(update_favorites, 5000);
    } else {
        clearInterval(timer);
    }
}

function delete_favorite(companyname)
{
    companyname = companyname.toUpperCase().trim();
    if (favorites.indexOf(companyname) >= 0)
    {
        favorites.splice(favorites.indexOf(companyname), 1);
        localStorage.setItem("favorites", JSON.stringify(favorites));
        update_favorites();
    }
}