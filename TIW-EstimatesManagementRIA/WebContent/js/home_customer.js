(function() { // avoid variables ending up in the global scope

    // page components
    var customerEstimatesList,estimateDetails, productList, productDetails, wizard,
      pageOrchestrator = new PageOrchestrator(); // main controller

    window.addEventListener("load", () => {
		if (sessionStorage.getItem("username") == null) {
			window.location.href = "index.html";
		} else {
			pageOrchestrator.start(); // initialize the components
			pageOrchestrator.refresh();
		} // display initial content
	}, false);
	
	// Constructors of view components

	function PersonalMessage(_username, messagecontainer) {
	    this.username = _username;
	    this.show = function() {
	      messagecontainer.textContent = this.username;
	    }
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
	      makeCall("GET", "GetMyEstimatesData", null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var estimatesToShow = JSON.parse(req.responseText);
	              if (estimatesToShow.length == 0) {
	                self.alert.textContent = "No estimate has been inserted yet!";
	                return;
	              }
	              self.update(estimatesToShow); // self visible by closure
	              if (next) next(); // show the default element of the list if present
	            }
	          } else {
	            self.alert.textContent = message;
	          }
	        }
		  );
		};

        this.update = function(arrayEstimates) {
            var row, idestimatecell, productnamecell, pricecell,detailcell, anchor;
            this.listcontainerbody.innerHTML = ""; // empty the table body
            // build updated list
            var self = this;
            arrayEstimates.forEach(function(estimate) { // self visible here, not this
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
                //anchor.estimateid = estimate.id; // make list item clickable
                anchor.setAttribute('estimateid', estimate.id); // set a custom HTML attribute
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
            var selector = "a[estimateid='" + estimateId + "']";
            var anchorToClick =
                (estimateId) ? document.querySelector(selector) : this.listcontainerbody.querySelectorAll("a")[0];
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
                        }
                    } else {
                        self.alert.textContent = message;
                    }
                }
            );
		};

        this.update = function(arrayProducts) {
            var row, idproductcell, productnamecell;
            this.listcontainerbody.innerHTML = ""; // empty the table body
            // build updated list
			var self = this;
			
			arrayProducts.forEach(function(product) { // self visible here, not this
				
                row = document.createElement("tr");
                idproductcell = document.createElement("td");
                idproductcell.textContent = product.id;
                row.appendChild(idproductcell);

                productnamecell = document.createElement("td");
                anchor = document.createElement("a");
                productnamecell.appendChild(anchor);
                //productnamecell.textContent = product.name;
                productText = document.createTextNode(product.name);
                anchor.appendChild(productText);
                anchor.setAttribute('productid', product.id); // set a custom HTML attribute
                anchor.addEventListener("click", (e) => {
					// dependency via module parameter
					
					//row.style.backgroundColor = "red";
					//self.reset();
					//	e.target.parentNode.parentNode.style.backgroundColor = "red";
					// TODO declare product details (IMAGE + OPTIONALS) on top of the current document
					// Fix the same thing also on CustomerEstimateList
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
            var anchorToClick =
				(productId) ? document.querySelector(selector) : this.listcontainerbody.querySelectorAll("a")[0];
            if (anchorToClick) {
				var rowOn = anchorToClick.parentNode.parentNode;
				rowOn.style.backgroundColor = "#b6cfff";
				anchorToClick.dispatchEvent(e);
			};
		}
	} //end ProductList()

	
	function EstimateDetails(options) {
	    this.alert = options['alert'];
	    this.estimatesTable = options['id_estimatetable'];
	    this.employee = options['employee'];
	    this.productid = options['productid'];
	    this.price = options['price'];
	    this.productname = options['productname'];
	    this.optionals = options['optionals'];
	    this.image = options['image'];
		
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
			// TODO
	    };

	    this.update = function(estimate) {
			
	    	this.optionals.innerHTML = ""; //empty the optional list
	    	if(estimate.employee != null){
	    		self.employee.textContent = estimate.employee.name + " " + estimate.employee.surname;
	    		self.price.textContent = estimate.price;
	    	}
	    	else{
	    		self.employee.textContent = "----";
	    		self.price.textContent = "Not priced yet";
	    	}
			
			
			self.productid.textContent = estimate.product.id;
	    	self.productname.textContent = estimate.product.name;
			//self.price.textContent = estimate.price;
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
	    };
	} // end EstimateDetails()

	
	function ProductDetails(options){

		this.alert = options['alert'];	//ci serve per identificare errore di input
		this.image = options['image'];
		this.optionalList = options['optionalList'];
		this.addestimateform = options['addestimateform'];

		this.registerEvents = function(orchestrator) {	//on click the customer adds a new estimate to be priced in the DB
			this.addestimateform.querySelector("input[type='button']").addEventListener('click', (e) => {
				var form = e.target.closest("form");
				if (form.checkValidity()) {
					var self = this,
					productToReport = form.querySelector("input[type = 'hidden']").value;
					makeCall("POST", 'AddEstimate', form,
						function(req) {
							if (req.readyState == 4) {
								var message = req.responseText;
								if (req.status == 200) {
									orchestrator.refresh(productToReport);
								}
								else {
									self.alert.textContent = message;
								}
							}
						}
					);
				} else {
					form.reportValidity();
				}
			});

			
		}

		this.show = function(product) {
			this.update(product);
		};

		this.reset = function() {
		      
		}

		this.update = function(product) {
			this.optionalList.innerHTML = ""; //empty the optional list
			document.getElementById("prdct").setAttribute("value",product.id);
			this.image.src = "images/".concat(product.image);
			var self = this;
			product.optionals.forEach(function(optional) {
			
				var li = document.createElement("li");
				var input = document.createElement("input");
				input.setAttribute("type","checkbox");
				input.setAttribute("value",optional.id);
				input.setAttribute("name","option[]");
				var textSpan = document.createElement("span");
				textSpan.textContent = optional.name;
				li.appendChild(input);
				li.appendChild(textSpan);
				
				if(optional.type == "SALE"){
					var saleSpan = document.createElement("span");
					saleSpan.setAttribute("class","sale");
					saleSpan.textContent = "SALE!";
					li.appendChild(saleSpan);
				}
				self.optionalList.appendChild(li);
			});
		}
	}//end ProductDetails()

    function PageOrchestrator() {
	    var alertContainer = document.getElementById("id_alert");
		
		this.start = function() {
			personalMessage = new PersonalMessage(sessionStorage.getItem('username'),
												document.getElementById("id_username"));
			personalMessage.show();

			customerEstimatesList = new CustomerEstimatesList(
				alertContainer,
				document.getElementById("id_estimatetable"),
				document.getElementById("id_estimatetablebody")
			);
			customerEstimatesList.show();

			estimateDetails = new EstimateDetails({
				alert: alertContainer,
				id_estimatetable: document.getElementById("id_estimatetable"),
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
			
			productDetails = new ProductDetails({ // many parameters, wrap them in an
			// object
				alert: alertContainer,
				image: document.getElementById("id_insert_product_img"),
				optionalList: document.getElementById("id_optionallist"),
				addestimateform: document.getElementById("id_addestimateform")
			});
			productDetails.registerEvents(this);

			/*wizard = new Wizard(document.getElementById("id_createmissionform"), alertContainer);
			wizard.registerEvents(this);
			*/
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
			}); // closure preserves visibility of this

			productList.reset();
			productDetails.reset();
			productList.show(function() {
				if(currentProduct == null){productList.autoclick()}
				else productList.autoclick(currentProduct.id);//currentProduct.id
			});
			//wizard.reset();
		};
    }
})();
