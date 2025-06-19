from fastapi import FastAPI
import uvicorn
from app.api.v1.endpoints import commodities
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI()

app.include_router(commodities.router,prefix="/api/v1/commodities",tags=["commodities"])

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/")
async def read_root():
    return {"Hello": "World"}

@app.get("/health")
async def health_check():
    return {"status": "healthy"}
if __name__ == "__main__":
    uvicorn.run("main:app", host="localhost", port=8000, reload=True)

