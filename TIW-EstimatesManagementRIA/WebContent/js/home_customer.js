(function() { // avoid variables ending up in the global scope

    // page components
    var estimateDetails, estimateList, wizard,
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
                // dependency via module parameter
                estimateDetails.show(e.target.getAttribute("estimateid")); // the list must know the details container
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

	function ProductList(_alert, _listcontainer, _listcontainerbody, _imagecontainer) {
	    this.alert = _alert;
	    this.listcontainer = _listcontainer;
		this.listcontainerbody = _listcontainerbody;
		this.image= _imagecontainer;

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
            var row, idproductcell, productnamecell, productimage;
            this.listcontainerbody.innerHTML = ""; // empty the table body
            // build updated list
			var self = this;

			//document.getElementById("id_insert_product_img").src = "images/".concat(arrayProducts[0].image);

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
				document.getElementById("id_insert_product_img").src = "images/".concat(product.image);
                // TODO declare product details (IMAGE + OPTIONALS) on top of the current document
                // Rename the next line with productDetails
                // Fix the same thing also on CustomerEstimateList           
                productDetails.show(e.target.getAttribute("productid")); // the list must know the details container
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
			console.log(this.listcontainerbody.querySelectorAll("a")[0]);
            if (anchorToClick) anchorToClick.dispatchEvent(e);
        };
	} //end ProductList()

	function EstimateDetails(options) {
	    this.alert = options['alert'];
	    this.detailcontainer = options['detailcontainer'];
	    this.expensecontainer = options['expensecontainer'];
	    this.expenseform = options['expenseform'];
	    this.closeform = options['closeform'];
	    this.date = options['date'];
	    this.destination = options['destination'];
	    this.status = options['status'];
	    this.description = options['description'];
	    this.country = options['country'];
	    this.province = options['province'];
	    this.city = options['city'];
	    this.fund = options['fund'];
	    this.food = options['food'];
	    this.accomodation = options['accomodation'];
	    this.travel = options['transportation'];

	    this.registerEvents = function(orchestrator) {
	      this.expenseform.querySelector("input[type='button']").addEventListener('click', (e) => {
	        var form = e.target.closest("form");
	        if (form.checkValidity()) {
	          var self = this,
	            missionToReport = form.querySelector("input[type = 'hidden']").value;
	          makeCall("POST", 'CreateExpensesReport', form,
	            function(req) {
	              if (req.readyState == 4) {
	                var message = req.responseText;
	                if (req.status == 200) {
	                  orchestrator.refresh(missionToReport);
	                } else {
	                  self.alert.textContent = message;
	                }
	              }
	            }
	          );
	        } else {
	          form.reportValidity();
	        }
	      });

	      this.closeform.querySelector("input[type='button']").addEventListener('click', (event) => {
	        var self = this,
	          form = event.target.closest("form"),
	          missionToClose = form.querySelector("input[type = 'hidden']").value;
	        makeCall("POST", 'CloseMission', form,
	          function(req) {
	            if (req.readyState == 4) {
	              var message = req.responseText;
	              if (req.status == 200) {
	                orchestrator.refresh(missionToClose);
	              } else {
	                self.alert.textContent = message;
	              }
	            }
	          }
	        );
	      });
	    }


	    this.show = function(missionid) {
	      var self = this;
	      makeCall("GET", "GetMissionDetailsData?missionid=" + missionid, null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var mission = JSON.parse(req.responseText);
	              self.update(mission); // self is the object on which the function
	              // is applied
	              self.detailcontainer.style.visibility = "visible";
	              
	            } else {
	              self.alert.textContent = message;

	            }
	          }
	        }
	      );
	    };


	    this.reset = function() {
	      
	    }

	    this.update = function(m) {
	      this.customer.textContent = m.customer;
	      this.productid.textContent = m.productid;
	      this.productname.textContent = m.status;
	      this.optional.textContent = m.description;
	    }
	}	//end EstimateDetail()

	function ProductDetails(options){

		this.alert = options['alert'];	//ci serve per identificare errore di input
	    this.detailcontainer = options['detailcontainer'];
	    this.expensecontainer = options['expensecontainer'];
	    this.expenseform = options['expenseform'];
	    this.closeform = options['closeform'];
	    this.date = options['date'];
	    this.destination = options['destination'];
	    this.status = options['status'];
	    this.description = options['description'];
	    this.country = options['country'];
	    this.province = options['province'];
	    this.city = options['city'];
	    this.fund = options['fund'];
	    this.food = options['food'];
	    this.accomodation = options['accomodation'];
	    this.travel = options['transportation'];

		this.registerEvents = function(orchestrator) {	//on click the customer adds a new estimate to be priced in the DB
			this.expenseform.querySelector("input[type='button']").addEventListener('click', (e) => {
				var form = e.target.closest("form");
				if (form.checkValidity()) {
					var self = this,
					missionToReport = form.querySelector("input[type = 'hidden']").value;
					makeCall("POST", 'CreateExpensesReport', form,
						function(req) {
							if (req.readyState == 4) {
								var message = req.responseText;
								if (req.status == 200) {
									orchestrator.refresh(missionToReport);
								} else {
									self.alert.textContent = message;
								}
							}
						}
					);
				} else {
					form.reportValidity();
				}
			});

			this.closeform.querySelector("input[type='button']").addEventListener('click', (event) => {
				var self = this,
				form = event.target.closest("form"),
				missionToClose = form.querySelector("input[type = 'hidden']").value;
				makeCall("POST", 'CloseMission', form,
					function(req) {
						if (req.readyState == 4) {
							var message = req.responseText;
							if (req.status == 200) {
								orchestrator.refresh(missionToClose);
							} else {
								self.alert.textContent = message;
							}
						}
					}
				);
			});
		}

		/*this.show = function(productid) {
			var self = this;
			makeCall("GET", "Get?productid=" + productid, null,	//TODO 
				//TODO show of img & optionals
			);
		};*/

		this.reset = function() {
			//TODO
		  }

		this.update = function(m) {
			//TODO 
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

			productList = new ProductList(
				alertContainer,
				document.getElementById("id_producttable"),
				document.getElementById("id_producttablebody"),
				document.getElementById("id_insert_product_img")
			);
			productList.show();

			/*
			missionDetails = new MissionDetails({ // many parameters, wrap them in an
			// object
			
			});
			missionDetails.registerEvents(this);*/

			/*
			productDetails = new ProductDetails({ // many parameters, wrap them in an
			// object
				alert: alertContainer,
				image: document.getElementById("id_insert_product_img"),
				optionalList: document.getElementById("id_optionallist"),
				detailcontainer: document.getElementById("id_detailcontainer")
			
			});
			productDetails.registerEvents(this);*/

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
			productList.reset();
			customerEstimatesList.show(function() {
				customerEstimatesList.autoclick(currentEstimate);
			}); // closure preserves visibility of this

			productList.show(function() {
				productList.autoclick(currentProduct);
			});
			//wizard.reset();
		};
    }
})();
