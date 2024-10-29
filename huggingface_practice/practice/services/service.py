import requests
from PIL import ImageDraw
import io
import base64
from PIL import ImageFont


token = "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"
headers = {"Authorization": f"Bearer {token}"}
API_URL = "https://api-inference.huggingface.co/models/facebook/detr-resnet-50"

# 글꼴 파일 경로와 크기 설정
font_path = "C:/Windows/Fonts/맑은 고딕/malgun.ttf"
font_size = 40  
font = ImageFont.truetype(font_path, font_size)

def query(image_bytes):
    response = requests.post(API_URL, headers=headers, data=image_bytes)
    return response.json()

def draw_boxes(image, outputs):
    draw = ImageDraw.Draw(image)
    for item in outputs:
        box = item['box']
        x1, y1, x2, y2 = box['xmin'], box['ymin'], box['xmax'], box['ymax']
        draw.rectangle([x1, y1, x2, y2], outline="blue", width=2)
        draw.text((x1, y1-10), item['label'], fill="red", font=font)
    return image

def convert_image_to_base64(image):
    buffered = io.BytesIO()
    image.save(buffered, format="PNG")
    return base64.b64encode(buffered.getvalue()).decode()