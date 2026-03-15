// Este código se ejecuta solo cuando el HTML está dibujado
document.addEventListener('DOMContentLoaded', () => {
    const fileInput = document.getElementById('file-input');
    const fileMsg = document.getElementById('file-msg');
    

    if (fileInput) {
        fileInput.addEventListener('change', () => {
            const fileName = fileInput.files[0]?.name || "sin archivo.";
            fileMsg.textContent = fileName;
            
        });
    }
});

document.addEventListener('DOMContentLoaded', () => {
    const fileInput = document.getElementById('file-input');
    const fileMsg = document.getElementById('file-msg');
    const removeBtn = document.getElementById('remove-file');
    const fileBox = document.getElementById('file-box');

    if (fileInput && fileMsg && removeBtn) {
        
        // Al cambiar el fichero
        fileInput.addEventListener('change', () => {
            if (fileInput.files.length > 0) {
                fileMsg.textContent = fileInput.files[0].name;
                fileBox.textContent = "";
                removeBtn.style.display = 'flex';
            }
        });

        removeBtn.addEventListener('click', (e) => {
            e.preventDefault();
            e.stopPropagation(); 
            
            fileInput.value = '';
            fileBox.textContent = "Elegir archivo";
            fileMsg.textContent = 'o arrastra y suelta aquí';
            removeBtn.style.display = 'none';
        });
    }
});