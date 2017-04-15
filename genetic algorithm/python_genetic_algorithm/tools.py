import numpy as np
import pandas as pd
from scipy.stats.stats import pearsonr
from math import sqrt
from sklearn.metrics import mean_squared_error
from sklearn.metrics import mean_absolute_error

def rmse_cal(prediction,target):
    return float(sqrt(mean_squared_error(prediction,target)))

def mae_cal(prediction, target):
    return float(mean_absolute_error(prediction,target))

def cor_cal(prediction,target):
    return pearsonr(prediction,target)

def mean_cal(list,n):
    return list.sum()/n

def frange(x, y, jump):
    while x < y:
        yield x
        x += jump

def df_select(input_df,num):
    """random sampling with replacement"""
    df=pd.DataFrame(columns=input_df.columns)
    #df.loc[0]=input_df.iloc[0]
    for i in range(num):
        df = pd.concat([df,input_df.sample(n=1)])
    return df
