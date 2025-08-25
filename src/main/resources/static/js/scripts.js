function copyToClipboard() {
    const shortUrlInput = document.getElementById('shortUrlInput');
    shortUrlInput.select();
    document.execCommand('copy');

    const button = event.currentTarget;
    const originalText = button.innerHTML;
    button.innerHTML = '<i class="bi bi-check2"></i> Copiado';

    setTimeout(() => {
        button.innerHTML = originalText;
    }, 2000);
}