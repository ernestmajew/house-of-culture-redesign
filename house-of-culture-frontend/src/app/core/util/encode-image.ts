export async function encodeFilesToBase64(fileList: File[]): Promise<string[]> {
    const base64Array: string[] = [];

    for (const file of fileList) {
        const base64String = await encodeFileToBase64(file);
        base64Array.push(base64String);
    }

    return base64Array;
}

export async function encodeFileToBase64(file: File): Promise<string> {
    return new Promise<string>((resolve, reject) => {
        const reader = new FileReader();

        reader.onload = () => {
            const base64String = reader.result as string;
            const sanitizedBase64String = base64String.split(',')[1];
            const finalBase64String = sanitizedBase64String.replace(/:/g, "");
            resolve(finalBase64String);
        };

        reader.onerror = (error) => {
            reject(error);
        };

        reader.readAsDataURL(file);
    });
}
