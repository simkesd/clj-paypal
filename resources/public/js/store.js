$(document).ready(function() {
  $('.item-wrapper').on('click', function() {
    var checked = $(this).find('input').prop('checked');
    $(this).find('input').prop('checked', !checked);
  });

$("#checkout-form").submit(function(e) {
      e.preventDefault();
      var checkedItems = $('input:checked');

  for(var i=0; i < checkedItems.length; i++) {
    $(checkedItems[i]).data('itemId');
    console.log($(checkedItems[i]).data('itemId'));
  }

      $.ajax({
          type: "POST",
          url: "http://localhost:3000/add-to-cart",
          data: 'data=[99, 123,2314,1345]',
          success: function (res) {
            console.log(res);
          }
        });

});

















});
