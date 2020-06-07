/* jshint esversion: 6 */

(function() { // avoid variables ending up in the global scope

    // Page components
    var customerEstimatesList, estimateDetails, productList, productDetails;
    var pageOrchestrator = new PageOrchestrator(); // Main controller

    window.addEventListener("load", () => {
        if (sessionStorage.getItem("username") == null) {
            window.location.href = "index.html";
        } else {
            pageOrchestrator.start(); // Initialize the components
            pageOrchestrator.refresh();
        } // Display initial content
    }, false);

    // Constructors of view components

    function PersonalMessage(_username, messagecontainer) {
          this.username = _username;
          this.show = function() {
              messagecontainer.textContent = this.username;
          };
      }

    function CustomerEstimatesList(_alert, _listcontainer, _listcontainerbody) {
        this.alert = _alert;
        this.listcontainer = _listcontainer;
        this.listcontainerbody = _listcontainerbody;

        this.reset = function() {
            this.listcontainer.style.visibility = "hidden";
        };

        this.show = function(next) {
            var self = this;
            makeCall("GET", "GetMyEstimatesData", null, function(req) {
                if (req.readyState == 4) {
                    var message = req.responseText;
                    
                    if (req.status == 200) {
                        var estimatesToShow = JSON.parse(req.responseText);
                        if (estimatesToShow.length == 0) {
                            self.alert.textContent = "No estimate has been inserted yet!";
                            self.reset();
                            return;
                        }
                        self.update(estimatesToShow); // self visible by closure
                        if (next) next(); // show the default element of the list if present
                    } else {
                        self.alert.textContent = message;
                    }
                }
            });
        };

        this.update = function(arrayEstimates) {
            var row, idestimatecell, productnamecell, pricecell,detailcell, anchor;
            this.listcontainerbody.innerHTML = ""; // Empty the table body
            var self = this;

            // Build updated list
            arrayEstimates.forEach(function(estimate) { // Self visible here, not this
                row = document.createElement("tr");
                idestimatecell = document.createElement("td");
                idestimatecell.textContent = estimate.id;
                row.appendChild(idestimatecell);

                productnamecell = document.createElement("td");
                productnamecell.textContent = estimate.product.name;
                row.appendChild(productnamecell);

                pricecell = document.createElement("td");
                pricecell.textContent = estimate.price;
                row.appendChild(pricecell);

                detailcell = document.createElement("td");
                anchor = document.createElement("a");
                detailcell.appendChild(anchor);
                detailText = document.createTextNode("Show");
                anchor.appendChild(detailText);

                anchor.setAttribute('data-estimateid', estimate.id); // Set a custom HTML attribute
                anchor.addEventListener("click", (e) => {
                    estimateDetails.show(estimate);

                    var children = Array.from(self.listcontainerbody.children);
                     children.forEach(function(child) {
                         child.style.backgroundColor = "white";
                     });
                    e.target.closest("tr").style.backgroundColor = "#b6cfff";
                }, false);

                anchor.href = "#";
                row.appendChild(detailcell);
                self.listcontainerbody.appendChild(row);
            });
            this.listcontainer.style.visibility = "visible";
        };

        this.autoclick = function(estimateId) {
            var e = new Event("click");
            var selector = "a[data-estimateid='" + estimateId + "']";
            var anchorToClick = (estimateId) ? document.querySelector(selector) : this.listcontainerbody.querySelectorAll("a")[0];

            if (anchorToClick) {
                var rowOn = anchorToClick.parentNode.parentNode;
                rowOn.style.backgroundColor = "#b6cfff";
                anchorToClick.dispatchEvent(e);
            }
        };
    }

    function ProductList(_alert, _listcontainer, _listcontainerbody) {
        this.alert = _alert;
        this.listcontainer = _listcontainer;
        this.listcontainerbody = _listcontainerbody;

        this.reset = function() {
          this.listcontainer.style.visibility = "hidden";
        };

        this.show = function(next) {
            var self = this;
            makeCall("GET", "GetProductList", null,
                function(req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;

                        if (req.status == 200) {
                            var productsToShow = JSON.parse(req.responseText);
                            if (productsToShow.length == 0) {
                                self.alert.textContent = "No products available!";
                                return;
                            }
                            self.update(productsToShow); // self visible by closure

                            if (next) {
                                next(); // show the default element of the list if present
                            }
                        } else {
                            self.alert.textContent = message;
                        }
                    }
                }
            );
        };

        this.update = function(arrayProducts) {
            var row, idproductcell, productnamecell;
            this.listcontainerbody.innerHTML = ""; // Empty the table body
            var self = this;

            // Build updated list
            arrayProducts.forEach(function(product) {

                row = document.createElement("tr");
                idproductcell = document.createElement("td");
                idproductcell.textContent = product.id;
                row.appendChild(idproductcell);

                productnamecell = document.createElement("td");
                anchor = document.createElement("a");
                productnamecell.appendChild(anchor);

                productText = document.createTextNode(product.name);
                anchor.appendChild(productText);
                anchor.setAttribute('productid', product.id);
                anchor.addEventListener("click", (e) => {

                    productDetails.show(product); // the list must know the details container

                     var children = Array.from(self.listcontainerbody.children);
                     children.forEach(function(child) {
                         child.style.backgroundColor = "white";
                     });

                    e.target.closest("tr").style.backgroundColor = "#b6cfff";
                }, false);
                anchor.href = "#";
                row.appendChild(productnamecell);

                self.listcontainerbody.appendChild(row);

            });
            this.listcontainer.style.visibility = "visible";
        };

        this.autoclick = function(productId) {
            var e = new Event("click");
            var selector = "a[productid='" + productId + "']";
            var anchorToClick = (productId) ? document.querySelector(selector) : this.listcontainerbody.querySelectorAll("a")[0];
            
            if (anchorToClick) {
                var rowOn = anchorToClick.parentNode.parentNode;
                rowOn.style.backgroundColor = "#b6cfff";
                anchorToClick.dispatchEvent(e);
            }
        };
    } // End ProductList()


    function EstimateDetails(options) {
        this.alert = options.alert;
        this.estimatestable = options.id_estimatetable;
        this.employee = options.employee;
        this.productid = options.productid;
        this.price = options.price;
        this.productname = options.productname;
        this.optionals = options.optionals;
        this.image = options.image;

        var self = this;

        this.show = function(estimate) {
            makeCall("GET", "GetEstimateDetails?estimateid=" + estimate.id, null, function(req) {
                if (req.readyState == 4) {
                    var message = req.responseText;

                    if (req.status == 200) {
                        var estimateWithDetails = JSON.parse(req.responseText);
                        self.update(estimateWithDetails);
                    } else {
                        self.alert.textContent = message;
                    }
                }
            });
        };

        this.reset = function() {
            this.estimatestable.style.visibility = "hidden";
            this.image.style.visibility = "hidden";
        };

        this.update = function(estimate) {
            this.optionals.innerHTML = ""; // Empty the optional list

            if (estimate.employee != null) {
                self.employee.textContent = estimate.employee.name + " " + estimate.employee.surname;
                self.price.textContent = estimate.price;
            } else {
                self.employee.textContent = "----";
                self.price.textContent = "Not priced yet";
            }

            self.productid.textContent = estimate.product.id;
            self.productname.textContent = estimate.product.name;
            self.image.setAttribute("src","images/".concat(estimate.product.image));

            estimate.product.optionals.forEach(function(opt) {
                var li = document.createElement("li");
                var textSpan = document.createElement("span");
                textSpan.textContent = opt.name;
                li.appendChild(textSpan);

                if (opt.type == "SALE"){
                    var saleSpan = document.createElement("span");
                    saleSpan.setAttribute("class","sale");
                    saleSpan.textContent = "SALE!";
                    li.appendChild(saleSpan);
                }
                self.optionals.appendChild(li);
            });
            this.estimatestable.style.visibility = "visible";
            this.image.style.visibility = "visible";
        };
    } // End EstimateDetails()


    function ProductDetails(options) {
        this.alert = options.alert;	// used to notify an input error
        this.image = options.image;
        this.optionalList = options.optionalList;
        this.addestimateform = options.addestimateform;

        this.registerEvents = function(orchestrator) {	// On click the customer adds a new estimate to be priced into the DB
            this.addestimateform.querySelector("input[type='button']").addEventListener('click', (e) => {
            	var nCheckedOptionals=document.querySelectorAll('input[type="checkbox"]:checked').length;	//calculate the number of checked checkboxes
            	var form = e.target.closest("form");

                if (form.checkValidity() && nCheckedOptionals>0) {
                    var self = this;
                    makeCall("POST", 'AddEstimate', form, function(req) {
                        if (req.readyState == 4) {
                            var message = req.responseText;

                            if (req.status == 200) {
                                orchestrator.refresh(message);
                            } else {
                                self.alert.textContent = message;
                            }
                        }
                    });
                } else {
                    form.reportValidity();
                    this.alert.textContent = "At least one optional has to be selected! "; //puts the error message inside the error placeholder
                }
            });
        };

        this.show = function(product) {
            this.update(product);
        };

        this.reset = function() {

        };

        this.update = function(product) {
            this.optionalList.innerHTML = ""; // Empty the optional list

            document.getElementById("prdct").setAttribute("value", product.id);
            this.image.src = "images/".concat(product.image);

            var self = this;
            product.optionals.forEach(function(optional) {

                var li = document.createElement("li");
                var input = document.createElement("input");
                
                input.setAttribute("type", "checkbox");
                input.setAttribute("value", optional.id);
                input.setAttribute("name", "option[]");
                
                var textSpan = document.createElement("span");
                textSpan.textContent = optional.name;
                li.appendChild(input);
                li.appendChild(textSpan);

                if (optional.type == "SALE") {
                    var saleSpan = document.createElement("span");
                    saleSpan.setAttribute("class","sale");
                    saleSpan.textContent = "SALE!";
                    li.appendChild(saleSpan);
                }
                self.optionalList.appendChild(li);
            });
        };
    } // End ProductDetails()

    function PageOrchestrator() {
        var alertContainer = document.getElementById("id_alert");

        this.start = function() {
            personalMessage = new PersonalMessage(
                sessionStorage.getItem('username'),
                document.getElementById("id_username")
            );
            personalMessage.show();

            customerEstimatesList = new CustomerEstimatesList(
                alertContainer,
                document.getElementById("id_estimatetable"),
                document.getElementById("id_estimatetablebody")
            );
            customerEstimatesList.show();

            estimateDetails = new EstimateDetails({ // many parameters, wrap them in an object
                alert: alertContainer,
                id_estimatetable: document.getElementById("id_estimate_details_table"),
                employee: document.getElementById("id_details_employeename"),
                productid: document.getElementById("id_details_productid"),
                price: document.getElementById("id_details_price"),
                productname: document.getElementById("id_details_productname"),
                optionals: document.getElementById("id_details_optionals"),
                image: document.getElementById("id_productimage")
            });

            productList = new ProductList(
                alertContainer,
                document.getElementById("id_producttable"),
                document.getElementById("id_producttablebody")
            );

            productDetails = new ProductDetails({ // many parameters, wrap them in an object
                alert: alertContainer,
                image: document.getElementById("id_insert_product_img"),
                optionalList: document.getElementById("id_optionallist"),
                addestimateform: document.getElementById("id_addestimateform")
            });
            productDetails.registerEvents(this);

            document.querySelector("a[href='Logout']").addEventListener('click', () => {
                window.sessionStorage.removeItem('username');
            });
        };


        this.refresh = function(currentEstimate, currentProduct) {
            alertContainer.textContent = "";
            customerEstimatesList.reset();
            estimateDetails.reset();

            customerEstimatesList.show(function() {
                customerEstimatesList.autoclick(currentEstimate);
            });

            productList.reset();
            productDetails.reset();
            productList.show(function() {
                if (currentProduct == null) {
                    productList.autoclick();
                } else {
                    productList.autoclick(currentProduct.id);
                }
            });
        };
    }
})();
