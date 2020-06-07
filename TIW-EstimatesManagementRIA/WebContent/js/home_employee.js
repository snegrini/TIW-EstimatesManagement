/* jshint esversion: 6 */

(function() { // Avoid variables ending up in the global scope

    var customerEstimatesList,estimateDetails, estimatesToPrice, estimateToPriceDetails; // Page components
    var pageOrchestrator = new PageOrchestrator(); // Main controller

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
			
	        makeCall("GET", "GetPricedEstimates", null, function(req) {
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
					} else {
						self.alert.textContent = message;
					}
				}
			});
		};

        this.update = function(arrayEstimates) {
			var row, idestimatecell, estimatecellname, pricecell, detailcell, anchor;

			this.listcontainerbody.innerHTML = ""; // Empty the table body

			// Build updated list
            var self = this;
            arrayEstimates.forEach(function(estimate) { // self visible here, not this
                row = document.createElement("tr");
                idestimatecell = document.createElement("td");
                idestimatecell.textContent = estimate.id;
                row.appendChild(idestimatecell);

                estimatecellname = document.createElement("td");
                estimatecellname.textContent = estimate.product.name;
                row.appendChild(estimatecellname);
                
                pricecell = document.createElement("td");
                pricecell.textContent = estimate.price;
                row.appendChild(pricecell);

                detailcell = document.createElement("td");
                anchor = document.createElement("a");
                detailcell.appendChild(anchor);
				detailText = document.createTextNode("Show");
				
                anchor.appendChild(detailText);
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
            if (anchorToClick) anchorToClick.dispatchEvent(e);
        };
	}

	function EstimatesToPrice(_alert, _listcontainer, _listcontainerbody) {
	    this.alert = _alert;
	    this.listcontainer = _listcontainer;
		this.listcontainerbody = _listcontainerbody;
		
	    this.reset = function() {
		    this.listcontainer.style.visibility = "hidden";
	    };

	    this.show = function(next) {
            var self = this;
            makeCall("GET", "GetNonPricedEstimates", null,
                function(req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var productsToShow = JSON.parse(req.responseText);
                            if (productsToShow.length == 0) {
                                self.alert.textContent = "There is no estimate to price!";
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

        this.update = function(estimateList) {
			var row, idestimatecell, estimatecellname;
			
            this.listcontainerbody.innerHTML = ""; // empty the table body
			var self = this;
			
			// Build updated list
			estimateList.forEach(function(estimate) { // self visible here, not this
				
                row = document.createElement("tr");
                idestimatecell = document.createElement("td");
                idestimatecell.textContent = estimate.id;
                row.appendChild(idestimatecell);

                estimatecellname = document.createElement("td");
                anchor = document.createElement("a");
                estimatecellname.appendChild(anchor);

				productText = document.createTextNode(estimate.product.name);
                anchor.appendChild(productText);
                anchor.addEventListener("click", (e) => {
                	
					estimateToPriceDetails.show(estimate);
					
					 var children = Array.from(self.listcontainerbody.children);
					 children.forEach(function(child) {
						 child.style.backgroundColor = "white";
					 });
					
					e.target.closest("tr").style.backgroundColor = "#b6cfff";
					
				}, false);
                anchor.href = "#";
                row.appendChild(estimatecellname);

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
			}
		};
	} // End EstimatesToPrice()

	
	function EstimateDetails(options) {
	    this.alert = options.alert;
	    this.estimatesTable = options.id_estimatetable;
	    this.customer = options.customer;
	    this.productid = options.productid;
	    this.price = options.price;
	    this.productname = options.productname;
	    this.optionals = options.optionals;
	    this.image = options.image;
		
		var self = this;
	    
	    this.show = function(estimate) {
	    	makeCall("GET", "GetEstimateDetailsEmployee?estimateid=" + estimate.id, null, function(req) {
				if (req.readyState == 4) {
					var message = req.responseText;

					if (req.status == 200) {
						var estimateWithDetails = JSON.parse(message);
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
			
        	self.optionals.innerHTML = "";
	    	self.customer.textContent = estimate.customer.name + " " + estimate.customer.surname;
			self.productid.textContent = estimate.product.id;
	    	self.productname.textContent = estimate.product.name;
			self.price.textContent = estimate.price;
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
	} // End EstimateDetails()

	
	function EstimateToPriceDetails(options){
		this.alert = options.alert;
		this.customername = options.customername;
		this.productid = options.productid;
		this.productname = options.productname;
		this.optionals = options.optional;
		this.image = options.image;
		this.priceestimateform = options.priceestimateform;

		var self = this;
		
		this.registerEvents = function(orchestrator) {	//on click the customer adds a new estimate to be priced in the DB
			this.priceestimateform.querySelector("input[type='button']").addEventListener('click', (e) => {
				var form = e.target.closest("form");
				if (form.checkValidity()) {
					var self = this,
					productToReport = form.querySelector("input[type = 'hidden']").value;
					makeCall("POST", 'AddEstimatePrice', form,
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
		};
		
		this.show = function(estimate) {
			makeCall("GET", "GetEstimateDetailsEmployee?estimateid=" + estimate.id, null, function(req) {
				if (req.readyState == 4) {
					var message = req.responseText;

					if (req.status == 200) {
						var estimateWithDetails = JSON.parse(message);
						self.update(estimateWithDetails);
					} else {
						self.alert.textContent = message;
					}
				}
			});
		};

		this.reset = function() {
		      
		};

		this.update = function(estimate) {
			document.getElementById("prdct").setAttribute("value",estimate.id);
			
        	this.optionals.innerHTML = "";
        	
			this.image.setAttribute("src","images/".concat(estimate.product.image));
			this.customername.textContent = estimate.customer.name + " " + estimate.customer.surname;
			this.productid.textContent = estimate.product.id;
			this.productname.textContent = estimate.product.name;
			
			var self = this;
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

	} // End EstimateToPriceDetails()

    function PageOrchestrator() {
	    var alertContainer = document.getElementById("id_alert");
		
		this.start = function() {
			personalMessage = new PersonalMessage(sessionStorage.getItem('username'),
												document.getElementById("id_username"));
			personalMessage.show();

			customerEstimatesList = new CustomerEstimatesList(
				alertContainer,
				document.getElementById("id_pricedestimatetable"),
				document.getElementById("id_pricedestimatetablebody")
			);
			customerEstimatesList.show();

			estimateDetails = new EstimateDetails({
				alert: alertContainer,
				id_estimatetable: document.getElementById("id_estimatetable"),
				customer: document.getElementById("id_details_customername"),
				productid: document.getElementById("id_details_productid"),
				price: document.getElementById("id_details_price"),
				productname: document.getElementById("id_details_productname"),
				optionals: document.getElementById("id_details_optionals"),
				image: document.getElementById("id_productimage")
			});
			
			estimatesToPrice = new EstimatesToPrice(
				alertContainer,
				document.getElementById("id_nonpricedestimatetable"),
				document.getElementById("id_nonpricedestimatetablebody")
			);
			
			estimateToPriceDetails = new EstimateToPriceDetails({ // many parameters, wrap them in an object
				alert: alertContainer,
				customername: document.getElementById("id_customername"),
				productid: document.getElementById("id_productid"),
				productname: document.getElementById("id_productname"),
				optional: document.getElementById("id_optional"),
				image: document.getElementById("id_npedetailsimage"),
				priceestimateform : document.getElementById("id_npedetailsform")
			});
			estimateToPriceDetails.registerEvents(this);

			document.querySelector("a[href='Logout']").addEventListener('click', () => {
				window.sessionStorage.removeItem('username');
			});
    	};
    	
    	
		this.refresh = function(currentEstimate, currentProduct) {
			alertContainer.textContent = "";
			customerEstimatesList.reset();
			//estimateDetails.reset();
			customerEstimatesList.show(function() {
				customerEstimatesList.autoclick(currentEstimate);
			}); // closure preserves visibility of this

			estimatesToPrice.reset();
			estimateToPriceDetails.reset();
			estimatesToPrice.show(function() {
				if (currentProduct == null) {
					estimatesToPrice.autoclick()
				} else {
					estimatesToPrice.autoclick(currentProduct.id);
				}
			});
		};
    }
})();