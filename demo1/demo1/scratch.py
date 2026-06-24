import os
import re

html_dir = r'd:\demo1 (2)\demo1\demo1\src\main\resources\templates'

pattern = re.compile(r'<a\s+[^>]*href=[\'\"](/[^\'\"]*dashboard)[\'\"][^>]*>.*?Profile.*?</a>', re.DOTALL | re.IGNORECASE)

def repl(m):
    anchor_text = m.group(0)
    if '#profile' not in m.group(1):
        new_href = m.group(1) + '#profile'
        return anchor_text.replace(m.group(1), new_href)
    return anchor_text

for fname in os.listdir(html_dir):
    if not fname.endswith('.html'): continue
    
    # Do not update the dashboard files themselves since their Profile links use # and onclick
    if fname in ['dashboard.html', 'manager-dashboard.html', 'staff-dashboard.html', 'tech-dashboard.html']:
        continue
        
    path = os.path.join(html_dir, fname)
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    new_content = pattern.sub(repl, content)
    
    if new_content != content:
        with open(path, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f'Updated {fname}')
