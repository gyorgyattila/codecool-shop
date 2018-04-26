function changeCartModal(items) {
    let cartItemsOutput =`<tr>
                                   <th>Product</th> 
                                       <th>Quantity</th>
                                        <th>Price</th>
                                   </tr>`;

    let totalPrice = 0;
    for (let item of items) {
        totalPrice+=item["defaultPrice"];
        cartItemsOutput+=`<tr>
                                <td>${item["name"]}</td>
                                <td><button class="incrementButton" data-product-id="${item["id"]}">+</button> <span class="quantity" data-product-id="${item["id"]}">${item["quantity"]}</span> <button class="decrementButton" data-product-id="${item["id"]}">-</button></td>
                                <td class="defaultPrice" data-default-price="${item["defaultPrice"]}">${item["defaultPrice"]}</td>
                            </tr>`;
    }
    document.getElementById("cartTableBody").innerHTML=cartItemsOutput;
    document.getElementById("totalPricePlace").innerHTML="<strong id='totalPrice'> Total price : " + totalPrice.toString()+"<strong>";
}

function addShoppingCartButtonListeners(){
    $(".incrementButton").on("click", function (event) {
        let productId = event.target.dataset.productId;
        let url = "/";
        $.ajax({
            type: "POST",
            data: {"id" : productId, "process": "increment"},
            url: url,
            success: function (quantityJSONString) {
                const quantity = JSON.parse(quantityJSONString);
                incrementNumberOfProduct(productId, quantity["quantity"])
            }
        })
    });
    $(".decrementButton").on("click", function (event) {
        const productId = event.target.dataset.productId;
        const url = "/";
        $.ajax({
            type: "POST",
            data: {"id" : productId, "process": "decrement"},
            url: url,
            success: function (quantityJSONString) {
                const quantity = JSON.parse(quantityJSONString);
                decrementNumberOfProduct(productId, quantity["quantity"])
            }
        })
    })
}

function incrementNumberOfProduct(productId, quantity) {
    const filter = "[data-product-id='" + productId + "']";
    $(".quantity").filter(filter).html(quantity);
    const prices = $(".defaultPrice");
    let totalPrice = 0;
    for (let price of prices) {
        totalPrice += parseFloat(price.dataset.defaultPrice)*quantity;
    }
    $("#totalPrice").html(totalPrice)
}

function decrementNumberOfProduct(productId, quantity) {
    const filter = "[data-product-id='" + productId + "']";
    $(".quantity").filter(filter).html(quantity);
    const prices = $(".defaultPrice");
    let totalPrice = 0;
    for (let price of prices) {
        totalPrice += parseFloat(price.dataset.defaultPrice)*quantity;
    }
    $("#totalPrice").html(totalPrice)
}