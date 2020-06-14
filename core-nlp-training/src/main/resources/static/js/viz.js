/**
 * 
 */

$( document ).ready(function() {
	
	
	$('#data-viz').click(function (e) {
		e.preventDefault();
		$('#data-viz123').html("<span class='spinner-border spinner-border-sm' role='status' aria-hidden='true'></span>");
		var tolerance = $('#tolerance').val();
		var model_name = $('#model_name').val();
		var train_file = $('#train_file').val();

		var form = $('#new-data-form')[0];

		var formData = new FormData(form);

		//console.log("upload data: " + formData['model_name']);
		resetCanvas()

		$.ajax({
			url: '/data/ner',
			data: formData,
			type: 'POST',
			enctype: 'multipart/form-data',
			processData: false,
			contentType: false,
			cache: false,
			timeout: 1000000,
			success: function (response) {
				//var returnedData = JSON.parse(response);
				console.log(response);
				// Bar chart
				
				var colorArray = Array.from({length: response.data.labels.length}, () => "#"+Math.floor(Math.random()*16777215).toString(16));
				
			      new Chart(document.getElementById("bar-chart"), {
			          type: 'bar',
			          data: {
			            labels: response.data.labels,
			            datasets: [
			              {
			                label: "Entity",
			                backgroundColor:colorArray,
			                data: response.data.labelCounts
			              }
			            ]
			          },
			          options: {
			            legend: { display: false },
			            title: {
			              display: true,
			              text: 'NER Entity Distribution'
			            }
			          }
			      });
			      
			      
			      new Chart(document.getElementById("pie-chart"), {
			    	    type: 'pie',
			    	    data: {
			    	      labels: response.data.labels,
			    	      datasets: [{
			    	        label: "Population (millions)",
			    	        backgroundColor: colorArray,
			    	        data: response.data.labelCounts
			    	      }]
			    	    },
			    	    options: {
			    	      title: {
			    	        display: true,
			    	        text: 'NER Entity Distribution'
			    	      }
			    	    }
			    	});


			},
			error: function (xhr, status, error) {
				errResponse =  JSON.parse(xhr.responseText)
				console.log(error);
			}
		});
	});
	
	
});


function resetCanvas(){
	$('#bar-container').html("");
	$('#pie-chart-container').html("");
	$('#bar-container').append('<canvas id="bar-chart" width="800" height="450"></canvas>');
	$('#pie-chart-container').append('<canvas id="pie-chart" width="800" height="450"></canvas>');
	};