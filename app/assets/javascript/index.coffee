$ ->
  $.get "/artists", (data) ->
    $.each data, (index, item) ->
      $("#artists").append "<li>Artist " + item.name + "</li>"