let currentPage = 1;
let currentWord = "";

function fetchImage(word, page) {
    if (!word) {
        showAlert("Query cannot be empty.", "warning");
        return;
    }

    fetch(`/api/v1/photos/${word}?page=${page}`, {method: "GET"
    })
        .then((response) => response.json())
        .then((data) => {
            if (data.photos && data.photos.length > 0) displayImage(data);
            else showAlert("No images found.", "warning");
        })
        .catch(() => showAlert("Error fetching images.", "danger"));
}

function displayImage(data) {
    const photo = data.photos[0];
    document.getElementById("image").src = photo.src.large2x;
    document.getElementById("photographer").innerHTML = `
        Photo by <a href="${photo.photographer_url}" target="_blank">${photo.photographer}</a>
    `;
    currentPage = data.page;
}

function prevImage() {
    if (currentPage > 1) fetchImage(currentWord, currentPage - 1);
    else showAlert("No previous images.", "info");
}

function nextImage() {
    fetchImage(currentWord, currentPage + 1);
}