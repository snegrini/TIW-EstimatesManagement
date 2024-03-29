/* jshint esversion: 6 */

(function() { // Avoid variables ending up in the global scope

    var pricedEstimatesList, estimateDetails, nonPricedEstimatesList, nonPricedEstimateDetails; // Page components
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

	function PricedEstimatesList(_alert, _listcontainer, _listcontainerbody) {
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
						var estimatesToShow = JSON.parse(message);
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

                detailcell = document.createElement("td");
                anchor = document.createElement("a");
                detailcell.appendChild(anchor);
				detailText = document.createTextNode("Show");
				
                anchor.appendChild(detailText);
				anchor.setAttribute('data-estimateid', estimate.id); // set a custom HTML attribute
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
				anchorToClick.dispatchEvent(e);
			}
        };
	} // End PricedEstimatesList()

	function NonPricedEstimatesList(_alert, _listcontainer, _listcontainerbody) {
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
                            var productsToShow = JSON.parse(message);
                            if (productsToShow.length == 0) {
                                self.alert.textContent = "There is no estimate to price!";
                                self.listcontainer.style.visibility = "hidden";
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
                	
					nonPricedEstimateDetails.show(estimate);
					
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
            var selector = "a[data-productid='" + productId + "']";
            var anchorToClick = (productId) ? document.querySelector(selector) : this.listcontainerbody.querySelectorAll("a")[0];
            if (anchorToClick) {
				anchorToClick.dispatchEvent(e);
			}
		};
	} // End NonPricedEstimatesList()

	
	function EstimateDetails(options) {
	    this.alert = options.alert;
	    this.estimatesTable = options.id_estimatetable;
	    this.customer = options.customer;
	    this.productid = options.productid;
	    this.price = options.price;
	    this.productname = options.productname;
	    this.optionals = options.optionals;
	    this.image = options.image;
	    this.detailscontainer = options.detailscontainer;
		
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
        	this.detailscontainer.style.visibility = "hidden";
	    };

	    this.update = function(estimate) {
			
	    	if(estimate != null){
	        	this.detailscontainer.style.visibility = "visible";
	    	}
	    	
        	self.optionals.innerHTML = "";
	    	self.customer.textContent = estimate.customer.name + " " + estimate.customer.surname;
			self.productid.textContent = estimate.product.id;
	    	self.productname.textContent = estimate.product.name;
			self.price.textContent = estimate.price + " €";
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

	
	function NonPricedEstimateDetails(options) {
		this.alert = options.alert;
		this.customername = options.customername;
		this.productid = options.productid;
		this.productname = options.productname;
		this.optionals = options.optional;
		this.image = options.image;
		this.priceestimateform = options.priceestimateform;
		this.detailstable = options.detailstable;

		var self = this;
		
        this.reset = function() {
        	this.customername.textContent = "";
        	this.productid.textContent = "";
        	this.productname.textContent = "";
        	this.optionals.innerHTML = "";
        	this.image.style.visibility = "hidden";
        	this.priceestimateform.style.visibility = "hidden";
        	this.priceestimateform.style.visibility = "hidden";
        	this.detailstable.style.visibility = "hidden";
        };
		
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

		this.update = function(estimate) {
			document.getElementById("prdct").setAttribute("value", estimate.id);
			
			if(estimate!=null) {
				this.priceestimateform.style.visibility = "visible";
	        	this.detailstable.style.visibility = "visible";

			}
			
        	this.optionals.innerHTML = "";
        	this.image.style.visibility = "visible";
			this.image.setAttribute("src", "images/".concat(estimate.product.image));
			this.customername.textContent = estimate.customer.name + " " + estimate.customer.surname;
			this.productid.textContent = estimate.product.id;
			this.productname.textContent = estimate.product.name;
			
			var self = this;
			var ul = document.createElement("ul");
			estimate.product.optionals.forEach(function(opt) {
				var li = document.createElement("li");
				var textSpan = document.createElement("span");
				textSpan.textContent = opt.name;
				li.appendChild(textSpan);
				
				if (opt.type == "SALE"){
					var saleSpan = document.createElement("span");
					saleSpan.setAttribute("class", "sale");
					saleSpan.textContent = "SALE!";
					li.appendChild(saleSpan);
				}
				ul.appendChild(li);
			});
			this.optionals.appendChild(ul);
		};

	} // End NonPricedEstimateDetails()

    function PageOrchestrator() {
	    var alertContainer = document.getElementById("id_alert");
	    var alertContainerNpe = document.getElementById("id_alert_npe");
	    var alertContainerNpeDetails = document.getElementById("id_alert_npe_details");
		
		this.start = function() {
			personalMessage = new PersonalMessage(sessionStorage.getItem('username'),
												document.getElementById("id_username"));
			personalMessage.show();

			pricedEstimatesList = new PricedEstimatesList(
				alertContainer,
				document.getElementById("id_pricedestimatetable"),
				document.getElementById("id_pricedestimatetablebody")
			);

			estimateDetails = new EstimateDetails({
				alert: alertContainer,
				id_estimatetable: document.getElementById("id_estimatetable"),
				customer: document.getElementById("id_details_customername"),
				productid: document.getElementById("id_details_productid"),
				price: document.getElementById("id_details_price"),
				productname: document.getElementById("id_details_productname"),
				optionals: document.getElementById("id_details_optionals"),
				image: document.getElementById("id_productimage"),
				detailscontainer: document.getElementById("id_details_container")
			});
			
			nonPricedEstimatesList = new NonPricedEstimatesList(
				alertContainerNpe,
				document.getElementById("id_nonpricedestimatetable"),
				document.getElementById("id_nonpricedestimatetablebody")
			);
			
			nonPricedEstimateDetails = new NonPricedEstimateDetails({ // many parameters, wrap them in an object
				alert: alertContainerNpeDetails,
				customername: document.getElementById("id_customername"),
				productid: document.getElementById("id_productid"),
				productname: document.getElementById("id_productname"),
				optional: document.getElementById("id_optional"),
				image: document.getElementById("id_npedetailsimage"),
				priceestimateform : document.getElementById("id_npedetailsform"),
				detailstable: document.getElementById("id_npedetailstable")
			});
			nonPricedEstimateDetails.registerEvents(this);

			document.querySelector("a[href='Logout']").addEventListener('click', () => {
				window.sessionStorage.removeItem('username');
			});
    	};
    	
    	
		this.refresh = function(currentEstimate, currentProduct) {
			alertContainer.textContent = "";
			alertContainerNpe.textContent = "";
			alertContainerNpeDetails.textContent = "";
			pricedEstimatesList.reset();	
			nonPricedEstimatesList.reset();
			nonPricedEstimateDetails.reset();
			estimateDetails.reset();

			pricedEstimatesList.show(function() {
				pricedEstimatesList.autoclick(currentEstimate);
			}); // closure preserves visibility of this

			nonPricedEstimatesList.show(function() {
				if (currentProduct == null) {
					nonPricedEstimatesList.autoclick()
				} else {
					nonPricedEstimatesList.autoclick(currentProduct.id);
				}
			});
		};
    }
})();