$(document).ready(function() {
  $('.item-wrapper').on('click', function() {
    var checked = $(this).find('input').prop('checked');
    $(this).find('input').prop('checked', !checked);
  });

  $("#checkout-form").submit(function(event) {
    event.preventDefault();

    var checkedItems = $('input:checked');

    var dataArray = [];
    for(var i=0; i < checkedItems.length; i++) {
      // $(checkedItems[i]).data('itemId');
      dataArray.push($(checkedItems[i]).data('item'));
    }

    console.log(dataArray);

    $.ajax({
      type: "POST",
      url: "http://localhost:3000/add-to-cart",
      data: 'data='+ JSON.stringify(dataArray),
      success: function (res) {
        console.log(res);
        document.location.href = context + "/cart";
      }
    });
  });

  $('.cart-item-removal').on('click', function() {
    var id = $(this).data('id');
    var row = $(this).closest('tr');
    $.ajax({
      type: "GET",
      url: "http://localhost:3000/cart/remove/"+id,
      success: function (res) {
        console.log(res);
        var json = jQuery.parseJSON(res);
        if(json.ok == 1 && json.n > 0) {
          $(row).remove();
        }else {
          console.log(json);
          console.log('Smths wrong!');
        }
        setFinalNumbers();
      }
    });
  });


  function getItems() {
    $.ajax({
      type: "GET",
      url: context + "/items",
      success: function (res) {
        console.log(res);
        document.location.href = context + "/dashboard";
      }
    });
  }

  function getShopingCartItems() {

  }

  function setFinalNumbers() {
    console.log('setting final numbers');
    var itemAmountElements = $('.item-amount');
    var itemPriceElements = $('.item-price');
    var totalAmount = 0;
    var totalPrice = 0;

    for(var i = 0; i < itemAmountElements.length; i++) {
      var amount = parseInt($(itemAmountElements[i]).html());
      var price = parseInt($(itemPriceElements[i]).html()) * amount;

      totalAmount += amount;
      totalPrice += price;
    }

    $('.total-amount span').html(totalAmount);
    $('.total-price span').html(totalPrice);

  }

  setFinalNumbers();



});
