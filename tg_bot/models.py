from pydantic import BaseModel, Field
from datetime import datetime
from typing import List, Optional
from decimal import Decimal


class CandleDto(BaseModel):
    open: float
    close: float
    high: float
    low: float
    volume: int
    time: datetime


class CandleAnomalyDto(BaseModel):
    name: str
    ticker: Optional[str] = None
    priceCurrent: float = Field(alias="priceCurrent")
    volume: int
    priceDailyChangeAsPercentage: float = Field(alias="priceDailyChangeAsPercentage")
    priceMinuteChangeAsPercentage: float = Field(alias="priceMinuteChangeAsPercentage")
    time: datetime
    candlesLastHour: List[CandleDto] = Field(alias="candlesLastHour")

    class Config:
        allow_population_by_field_name = True
        json_encoders = {
            datetime: lambda v: v.isoformat()
        }