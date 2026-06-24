const fs = require('fs');
const path = require('path');

const htmlDir = 'd:\\demo1 (2)\\demo1\\demo1\\src\\main\\resources\\templates';

// We want to match an anchor tag that points to dashboard AND contains Profile INSIDE it.
// The regex should not span across multiple <a> tags.
// [^>]* matches everything but >. Once we are inside the anchor, we can match anything but </a>.
const pattern = /<a\s+[^>]*href=['"](\/[^'"]*dashboard)(#profile)?['"][^>]*>(?:(?!<\/a>)[\s\S])*Profile(?:(?!<\/a>)[\s\S])*<\/a>/gi;

function repl(match, p1, p2) {
    if (!p2) { // If it doesn't already have #profile
        const newHref = p1 + '#profile';
        return match.replace(p1, newHref);
    }
    return match;
}

// We also need to PREVENT the wrong ones that were mistakenly added!
// We will simply remove ALL #profile from any dashboard links that do NOT have Profile in them!
const removePattern = /<a\s+[^>]*href=['"](\/[^'"]*dashboard)#profile['"][^>]*>(?:(?!<\/a>)[\s\S])*Dashboard(?:(?!<\/a>)[\s\S])*<\/a>/gi;

function replRemove(match, p1) {
    // p1 is the dashboard URL without #profile
    return match.replace(p1 + '#profile', p1);
}

const files = fs.readdirSync(htmlDir);
for (const fname of files) {
    if (!fname.endsWith('.html')) continue;
    
    // Ignore the main dashboard files
    if (['dashboard.html', 'manager-dashboard.html', 'staff-dashboard.html', 'tech-dashboard.html'].includes(fname)) {
        continue;
    }
    
    const filePath = path.join(htmlDir, fname);
    const content = fs.readFileSync(filePath, 'utf8');
    
    let newContent = content.replace(removePattern, replRemove);
    newContent = newContent.replace(pattern, repl);
    
    if (newContent !== content) {
        fs.writeFileSync(filePath, newContent, 'utf8');
        console.log(`Updated ${fname}`);
    }
}
