$( document ).ready(function() {

	//Adding file name to upload panel
	$(".custom-file-input").on("change", function () {
		var fileName = $(this).val().split("\\").pop();
		$(this).siblings(".custom-file-label").addClass("selected").html(fileName);
	});


	$("#new-training").click(function (e) {
		var s = '<div class="overlay spinner-grow text-primary spinner-grow-lg mt-2"><span class="sr-only">Loading...</span></div>';
		$( '.table-responsive' ).find( 'tbody' ).append( '<div class="overlay-spinner text-center">' + s + '</div>' );
		setTimeout(function () {
			$.ajax({
				url: '/training/ner',
				type: 'GET',
				contentType: "application/json",
				success: function (response) {
					//var returnedData = JSON.parse(response);
					console.log(response);
					
					var history_update = "";
					$.each(response.jobDetails, function (i, value) {
						console.log(i + " : " + value['execution_id'] + ": " + value['job_status']);
						var disabled = "";
						if(value['job_status'] === "COMPLETED"){
							disabled = "disabled"
						}
						history_update = history_update +
						"<tr id='job_" + value['execution_id'] + "'>" +
						"<td>" + value['execution_id'] + "</td>" +
						"<td>NER</td>"+
						"<td id='status_" + value['execution_id'] + "'>" + value['job_status'] + "</td>" +
						"<td class='d-none d-sm-table-cell'><button "+disabled+" id='refresh_" + value['execution_id'] + "' type='button' class='btn btn-primary jobrefresh' data-value=" + value['execution_id'] + ">refresh</button></td>" +
						"<td class='d-none d-sm-table-cell'><button id='download_" + value['execution_id'] + "' type='button' class='btn btn-primary download' data-value=" + value['execution_id'] + ">download</button></td>" +
						"</tr>"
					});

					$('#job-history').html(history_update)


				},
				error: function (error) {
					console.log(error);
				}
			});
		}, 1000);
	});


	$("#menu-toggle").click(function (e) {
		e.preventDefault();
		$("#wrapper").toggleClass("toggled");
	});


	$(function () {
		$('#new-training').click(function (e) {
			e.preventDefault();
			$('#new-training').html("<span class='spinner-border spinner-border-sm' role='status' aria-hidden='true'></span>");
			var tolerance = $('#tolerance').val();
			var model_name = $('#model_name').val();
			var train_file = $('#train_file').val();

			var form = $('#new-training-form')[0];

			var formData = new FormData(form);

			console.log("upload data: " + formData['model_name']);


			$.ajax({
				url: '/training/ner',
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
					setTimeout(function () {
						$('#new-training').html("execute")
						$('#trainingToast').css("background-color","#40CB94");
						$('#toast-message').html("Success! Training Job Id:" + response.jobDetail.execution_id + " created");
						$('#trainingToast').toast({
							delay: 10000
						});
						$('#trainingToast').toast('show');
					}, 10);


				},
				error: function (xhr, status, error) {
					errResponse =  JSON.parse(xhr.responseText)
					$('#new-training').html("execute");
					$('#trainingToast').css("background-color","#FDDCDC");
					$('#toast-message').html("<strong>Failed!</strong> "+errResponse.errorMessage);
					//$("#new-success").removeClass("alert-success");
					//$("#new-success").addClass("alert-error");
					$('#trainingToast').toast({
						delay: 10000
					});
					$('#trainingToast').toast('show');
					console.log(error);
				}
			});
		});
	});


	$(document).on("click", ".jobrefresh", function (e) {
		$.ajax({
			url: '/training/ner/' + $(this).data("value"),
			type: 'GET',
			contentType: "application/json",
			success: function (response) {
				//var returnedData = JSON.parse(response);
				console.log(response);
				setTimeout(function () {
					console.log('#status_' + response.jobDetail.execution_id);
					console.log(response.jobDetail.job_status);
					$('#status_' + response.jobDetail.execution_id).html(response.jobDetail.job_status)
				}, 10);


			},
			error: function (error) {
				console.log(error);
			}
		});
	});

	$(document).on("click", ".download", function (e) {
		$.ajax({
			url: '/training/ner/' + $(this).data("value") + '/download',
			type: 'GET',
			contentType: "application/gz",
			success: function (response) {
				//var returnedData = JSON.parse(response);
				//console.log(response);
				setTimeout(function () {
					//var blob = new Blob([response],  {type: 'zip'});
					window.open(response);

				}, 10);


			},
			error: function (error) {
				console.log(error);
			}
		});
	});

});