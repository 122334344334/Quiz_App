import sys, os, textwrap

PAGE_WIDTH = 595  # A4 width in points
PAGE_HEIGHT = 842  # A4 height in points
MARGIN_LEFT = 50
MARGIN_TOP = 50
FONT_SIZE = 12
LINE_HEIGHT = FONT_SIZE + 4

texts = []
for i in range(1,5):
    with open(f'description_page{i}.txt', 'r', encoding='utf-8') as f:
        texts.append(f.read())

# PDF helper to create objects
objects = []

# Font object (Helvetica)
font_obj_index = len(objects) + 1
objects.append('<< /Type /Font /Subtype /Type1 /Name /F1 /BaseFont /Helvetica >>')

pages_refs = []
content_refs = []

for page_text in texts:
    lines = textwrap.wrap(page_text, width=90)
    content_stream = ['BT', f'/F1 {FONT_SIZE} Tf', f'{MARGIN_LEFT} {PAGE_HEIGHT - MARGIN_TOP} Td']
    first = True
    y = PAGE_HEIGHT - MARGIN_TOP
    for line in lines:
        if not first:
            y -= LINE_HEIGHT
            content_stream.append(f'{MARGIN_LEFT} {y} Td')
        first = False
        sanitized = line.replace('(', '\(').replace(')', '\)')
        content_stream.append(f'({sanitized}) Tj')
    content_stream.append('ET')
    content_string = '\n'.join(content_stream)
    content_obj_index = len(objects) + 1
    objects.append(f'<< /Length {len(content_string.encode("utf-8"))} >>\nstream\n{content_string}\nendstream')
    content_refs.append(content_obj_index)
    page_obj_index = len(objects) + 1
    objects.append(f'<< /Type /Page /Parent 0 0 R /MediaBox [0 0 {PAGE_WIDTH} {PAGE_HEIGHT}] /Resources << /Font << /F1 {font_obj_index} 0 R >> >> /Contents {content_obj_index} 0 R >>')
    pages_refs.append(page_obj_index)

# Build Pages object
pages_obj_index = len(objects) + 1
kids = ' '.join(f'{ref} 0 R' for ref in pages_refs)
objects.append(f'<< /Type /Pages /Kids [ {kids} ] /Count {len(pages_refs)} >>')

# Fix parent references (replace 0 0 R placeholder with pages_obj_index)
for idx in pages_refs:
    objects[idx-1] = objects[idx-1].replace('/Parent 0 0 R', f'/Parent {pages_obj_index} 0 R')

# Catalog object
catalog_obj_index = len(objects) + 1
objects.append(f'<< /Type /Catalog /Pages {pages_obj_index} 0 R >>')

# Write PDF
positions = []
result = ['%PDF-1.4']
for obj_index, obj in enumerate(objects, start=1):
    positions.append(len('\n'.join(result).encode('utf-8')) + 1)  # position of start of object
    result.append(f'{obj_index} 0 obj')
    result.append(obj)
    result.append('endobj')

xref_start = len('\n'.join(result).encode('utf-8')) + 1
result.append('xref')
result.append(f'0 {len(objects)+1}')
result.append('0000000000 65535 f ')
for pos in positions:
    result.append(f'{pos:010} 00000 n ')
result.append('trailer')
result.append(f'<< /Size {len(objects)+1} /Root {catalog_obj_index} 0 R >>')
result.append('startxref')
result.append(str(xref_start))
result.append('%%EOF')

pdf_data = '\n'.join(result).encode('utf-8')
with open('Quiz_App_Beschreibung.pdf', 'wb') as f:
    f.write(pdf_data)
print('PDF generated')
