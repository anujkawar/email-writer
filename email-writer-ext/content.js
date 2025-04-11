console.log("Chal raha hai");

//Creating AI reply button
function createAIButton(){
   const button = document.createElement('div');
   button.className = 'T-I J-J5-Ji aoO v7 T-I-atl L3';
   button.style.marginRight = '8px';
   button.innerHTML = 'AI Reply';
   button.setAttribute('role', 'button');
   //button.setAttribute('data=tooltip', 'Generate AI Reply');
   button.setAttribute('data-tooltip','Generate AI Reply');
   return button;
}

//Get Email content amd return its text
function getEmailContent() {
    const selectors = [
        '.h7',
        '.a3s aiL',
        'gmail_quote',
        '[role="presentation"]'
    ];

    for(const selector of selectors){
        const content = document.querySelector(selector);
        if(content){
            return content.innerText.trim();
        }
        return '';
    }
}

//Find toolbar and return 
function findComposeToolbar() {
    const selectors = [
        '.btC',
        '.aDh',
        '[role="toolbar"]',
        '.gU.Up'
    ];

    for(const selector of selectors){
        const toolbar = document.querySelector(selector);
        if(toolbar){
            return toolbar
        }
        return null;
    }
}

function injectButton() {
    console.log("injectButton function called");

    //Check if AI Reply exist then remove 
    const existingButton = document.querySelector('.ai-reply-button');
    if(existingButton) existingButton.remove();

    const toolbar = findComposeToolbar();
    if(!toolbar){
        console.log("Toolbar not found");
        return;
    }

    console.log("Toolbar found, creating AI button");

    //Add class for button
    const button = createAIButton();
    button.classList.add('.ai-reply-button');

    //For backend API call, get response and inject it tree document module 
    button.addEventListener('click', async (params) => {
        try {
            button.innerHTML = 'Generating...'
            button.disabled = true;

            const emailContent =  getEmailContent();

            const response =  await fetch('http://localhost:8080/api/email/generate',{
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    emailContent: emailContent,
                    tone: "professional"

                })
            });

            if(!response.ok){
                throw new Error('API Request Failed')
            }

            const generatedReply =  await response.text();
            const composeBox = document.querySelector('[role="textbox"][g_editable="true"]');

            if(composeBox){
                composeBox.focus();
                document.execCommand('insertText', false, generatedReply);
            }else{
                console.error('Compose box not found');
            }
        } catch (error) {
            console.error(error);
            alert('Failed to generate reply');
        }finally{
            button.innerHTML = 'AI Reply';
            button.disabled = false
        }
        
    });

    //It will insert button in the beginning of toolbar
    toolbar.insertBefore(button,toolbar.firstChild);

}

const observer = new MutationObserver((mutations) => {
    for(const mutation of mutations){
        const addedNodes = Array.from(mutation.addedNodes);
        const hasComposElement = addedNodes.some(node =>
            node.nodeType === Node.ELEMENT_NODE && 
            (node.matches('.aDh, .btc, [role="dialog"]') || node.querySelector('.aDh, .btc, [role="dialog"]'))
        );

        if(hasComposElement){
            console.log("Compose window detected");
            setTimeout(injectButton, 500);
        }
    }

});

observer.observe(document.body, {
    childList: true,
    subtree: true
});