// Pie Chart Example
function renderPieChart(pieLabels, pieData) {
    var ctx = document.getElementById("myPieChart");
    var pieLabelsArray = pieLabels.split(',');
    var pieDataArray = pieData.split(',').map(Number);

    var myPieChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: pieLabelsArray,
            datasets: [{
                data: pieDataArray,
                backgroundColor: ['#6870DD', '#1cc88a', '#36b9cc'],
                hoverBackgroundColor: ['#6870DD', '#17a673', '#2c9faf'],
                hoverBorderColor: "rgba(234, 236, 244, 1)",
            }],
        },
        options: {
            maintainAspectRatio: false,
            tooltips: {
                backgroundColor: "rgb(255,255,255)",
                bodyFontColor: "#858796",
                borderColor: '#dddfeb',
                borderWidth: 1,
                xPadding: 15,
                yPadding: 15,
                displayColors: false,
                caretPadding: 10,
            },
            legend: {
                display: false
            },
            cutoutPercentage: 80,
        },
    });
}
