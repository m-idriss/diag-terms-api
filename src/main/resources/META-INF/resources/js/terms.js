let jsonResult = ''; // Store the result in a variable

// Ensure DOM is loaded before accessing elements
window.onload = function () {
    const inputField = document.getElementById("word_name");

    if (inputField) {
        const wordValue = inputField.value;
        console.log("Input word value:", wordValue); // Debugging output
        if (wordValue) {
            fetchTerm("GET", `/api/v1/terms/${wordValue}`, "result");
            fetchImage(wordValue, 1); // Fetch the first image
            currentWord = inputField.value;
        }
    } else {
        console.error('Input field with id="word_name" not found.');
    }

    // Fetch all terms to populate the word list
    fetchTerm("GET", "/api/v1/terms", "all-terms-list");
};


function getWord() {
    const wordValue = document.getElementById("word_name").value;
    currentWord = wordValue;
    sendWord(wordValue);
    fetchImage(wordValue, 1);
}

function sendWord(word) {
    fetchTerm("GET", "/api/v1/terms/" + word, "result");
    setTimeout(() => {
        fetchTerm("GET", "/api/v1/terms", "all-terms-list");
    }, 1500);
}

function fetchTerm(verb, url, containerId) {
    const xhttp = new XMLHttpRequest();

    xhttp.open(verb, url, true);
    xhttp.setRequestHeader("Content-type", "application/json");

    xhttp.onreadystatechange = () => {
        if (xhttp.readyState === 4) {
            const responseContainer = document.getElementById(containerId);
            responseContainer.innerHTML = ""; // Clear previous content

            const jsonResponse = JSON.parse(xhttp.responseText);

            if (containerId === "all-terms-list") {
                updateAllTermsList(jsonResponse);
            } else if (containerId === "result") {
                if (jsonResponse.synonyms && jsonResponse.synonyms.length > 0) {
                    updateWordDetails(jsonResponse);
                } else {
                    displayNotFoundMessage(responseContainer);
                }
            }
        }
    };

    xhttp.send();
}

function displayNotFoundMessage(container) {
    const message = document.createElement("div");
    message.className = "alert alert-warning";
    message.textContent = "No synonyms found for this word.";
    container.appendChild(message);
}


// UI-related functions
function createWordButton(term) {
    const wordButton = document.createElement("button");
    wordButton.className = "btn term-btn"; // Apply the same class as synonyms
    wordButton.innerHTML = term.word;
    wordButton.addEventListener("click", () => {
        document.getElementById("word_name").value = term.word;
        getWord();
    });
    return wordButton;
}

function updateAllTermsList(response) {
    const allTermsListContainer = document.getElementById("all-terms-list");
    allTermsListContainer.innerHTML = "";

    const terms = response._embedded.terms;

    // Sort terms alphabetically by word
    terms.sort((a, b) => a.word.localeCompare(b.word));

    terms.forEach((term) => {
        const wordButton = createWordButton(term);
        allTermsListContainer.appendChild(wordButton);
    });
}

function updateWordDetails(response) {
    const resultContainer = document.getElementById("result");
    resultContainer.innerHTML = ""; // Clear previous content

    const synonyms = response.synonyms; // Assuming synonyms is an array in the response

    synonyms.forEach((synonym) => {
        const synonymButton = document.createElement("button");
        synonymButton.className = "btn synonym-btn";
        synonymButton.innerHTML = synonym;
        synonymButton.addEventListener("click", () => {
            document.getElementById("word_name").value = synonym;
            getWord();
        });
        resultContainer.appendChild(synonymButton);
    });
}

function copyResultToClipboard() {
    if (jsonResult) {
        navigator.clipboard
            .writeText(jsonResult) // Use the stored JSON result
            .then(() => {
                alert("Result copied to clipboard!");
            })
            .catch((err) => {
                console.error("Unable to copy to clipboard", err);
            });
    } else {
        alert("No JSON result to copy.");
    }
}

function deleteWord() {
    const word = document.getElementById("word_name").value;
    if (word) {
        const url = `/api/v1/terms/${word}`;
        const xhttp = new XMLHttpRequest();

        xhttp.open("DELETE", url, true);
        xhttp.setRequestHeader("Content-type", "application/json");

        xhttp.onreadystatechange = () => {
            if (xhttp.readyState === 4 && xhttp.status === 200) {
                alert(`Term "${word}" deleted successfully.`);
                document.getElementById("word_name").value = ""; // Clear input
                fetchTerm("GET", "/api/v1/terms", "all-terms-list"); // Refresh word list
            }
        };

        xhttp.send();
    }
}
