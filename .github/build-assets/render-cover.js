const puppeteer = require('puppeteer');
const path = require('path');

(async () => {
    const browser = await puppeteer.launch({ args: ['--no-sandbox', '--disable-setuid-sandbox'] });
    const page = await browser.newPage();

    // Resolve exact file paths
    const htmlUrl = 'file://' + path.resolve(process.argv[2]);
    const pdfPath = path.resolve(process.argv[3]);

    // Load as a real webpage and wait until all images/fonts are downloaded
    await page.goto(htmlUrl, { waitUntil: 'networkidle0' });

    // Force 16x9 and background graphics
    await page.pdf({
        path: pdfPath,
        width: '210mm',
        height: '118mm',
        printBackground: true,
        margin: { top: 0, right: 0, bottom: 0, left: 0 }
    });

    await browser.close();
})();