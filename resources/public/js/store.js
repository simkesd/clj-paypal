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
    
    // $.post( "http://localhost:3000/add-to-cart",  'data=[{"item-id" : "999", "amount":"1"},{"item-id" : "789", "amount":"1"}]');


console.log(dataArray);

    $.ajax({
      type: "POST",
      url: "http://localhost:3000/add-to-cart",
      // data: 'data=[{"item-id" : "999", "amount":"1"},{"item-id" : "789", "amount":"1"}]',
      data: 'data='+ JSON.stringify(dataArray),
      success: function (res) {
        console.log(res);
      }
    });



  });

  // $.ajax({
  //   type: "POST",
  //   url: "http://localhost:3000/add-to-cart",
  //   data: 'data=[{"id" : "999", "finishe":"false"},{"id" : "789", "finished":"false"}]',
  //   success: function (res) {
  //     console.log(res);
  //   }
  // });




});
