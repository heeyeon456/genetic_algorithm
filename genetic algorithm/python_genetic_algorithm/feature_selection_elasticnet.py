import pandas as pd
from sklearn.linear_model import ElasticNetCV
from sklearn.model_selection import KFold
from numpy.random import RandomState
from numpy import nonzero
import sys
from tools import rmse_cal,mae_cal,cor_cal,mean_cal,frange,df_select
from feature_selection_cor import calculate_corr,calculate_corr_cut_by_num, \
make_df_by_selected,select_by_f_test

def change_gender_info(input_data):

    input_data.replace('female',-1,inplace=True)
    input_data.replace('male',0,inplace=True)
    input_data.ix[25979,0]='gender'
    #input.to_csv('combined_dataset/platform_common/training/liver_common_train_2.tsv',sep='\t',index=False, na_rep='NA')

    return input_data


def make_df(tissue,feature_cor):
    """make dataframe with selected feature"""

    input_data=pd.read_table('combined_dataset/platform_common/training2/'+tissue+'_common_train.tsv',sep='\t')

    result_feature=pd.merge(input_data,feature_cor,how="inner",on=['Composite Element REF'])
    #age
    result_feature=result_feature.append(input_data.iloc[len(input_data)-2,:],ignore_index=True)
    #gender
    #result_feature=result_feature.append(input_data.iloc[len(input_data)-1,:],ignore_index=True)
    result_feature = result_feature.drop(['count'], axis=1)

    return result_feature


def make_df2(tissue,feature_cor):
    """make dataframe with selected feature"""

    input_data=pd.read_table('combined_dataset/platform_common/training2/'+tissue+'_common_train.tsv',sep='\t')

    result_feature=pd.merge(input_data,feature_cor,how="inner",on=['Composite Element REF'])
    #age
    result_feature=result_feature.append(input_data.iloc[len(input_data)-1,:],ignore_index=True)
    #gender
    #result_feature=result_feature.append(input_data.iloc[len(input_data)-1,:],ignore_index=True)
    result_feature = result_feature.drop(['count'], axis=1)

    return result_feature


def elastic_net_l1(input_data,l1):
    """elastic net 10-fold crossvalidation"""

    kf=KFold(n_splits=10,shuffle=True)
    input_t=input_data.ix[:,1:]
    final_input=input_t.transpose()
    final_input.fillna(0,inplace=True)



    pred=pd.DataFrame()
    real=pd.DataFrame()

    elastic_net=ElasticNetCV(cv=kf, fit_intercept=True, random_state=RandomState(None), selection='random',
                             max_iter=100000,l1_ratio=l1)

    prediction=pd.DataFrame(columns=['predict','real'])



    for train, test in kf.split(final_input):

        x_train=final_input.iloc[train,:-1].astype('float64').values
        y_train=final_input.iloc[train,-1].astype('float64').values
        elastic_net.fit(x_train,y_train)

        x_test=final_input.iloc[test,:-1].astype('float64').values
        y_test=final_input.iloc[test,-1].astype('float64').values

        pred=pred.append(pd.DataFrame(elastic_net.predict(x_test)))
        real=real.append(pd.DataFrame(y_test))


    prediction=pd.concat([pred,real],axis=1)


    rmse=rmse_cal(prediction.iloc[:,0],prediction.iloc[:,1])
    cor=cor_cal(prediction.iloc[:,0],prediction.iloc[:,1])

    return l1,rmse,cor

def find_l1(input_data):
    """choose elastic net l1 ratio"""

    list=['l1','rmse','cor']
    by_l1_ratio=pd.DataFrame(columns=list)
    num=0
    for l in frange(0.1,1,0.1):
        el=elastic_net_l1(input_data,l)
        by_l1_ratio.loc[num]=el

        num=num+1
    #print(by_l1_ratio)

    return by_l1_ratio.loc[by_l1_ratio['rmse'].idxmin()]


def feature_by_elastic_net(input_data,l1,feature_vector):

    kf=KFold(n_splits=10)

    elastic_net=ElasticNetCV(cv=kf, fit_intercept=True, random_state=RandomState(None), selection='random',
                             max_iter=100000,l1_ratio=l1)

    non_zero_features=pd.DataFrame(columns=['Composite Element REF','count'])

    train=int(len(input_data)*0.9)
    x_train=input_data.iloc[1:train,:-1].astype('float64').values
    y_train=input_data.iloc[1:train,-1].astype('float64').values
    elastic_net.fit(x_train,y_train)

    x_test=input_data.iloc[train:,:-1].astype('float64').values
    y_test=input_data.iloc[train:,-1].astype('float64').values

    non_zero_features.iloc[:,0] = feature_vector[nonzero(elastic_net.coef_)]
    non_zero_features.fillna(0,inplace=True)

    return pd.DataFrame(data=non_zero_features,columns=non_zero_features.columns)



def compare_cpg(feature1, feature2):
    feature=pd.DataFrame(columns=['Composite Element REF','count'])

    a=feature1['Composite Element REF'].isin(feature2['Composite Element REF'])
    b=feature2['Composite Element REF'].isin(feature1['Composite Element REF'])
    a_in=a.index.values
    b_in=b.index.values
    num=0
    j=0
    for i in a_in:
        if a[i] == True:
            feature.loc[num]=feature1.loc[i]
            feature.ix[num,1]=feature1.ix[i,1]+1
        elif a[i] == False:
            feature.loc[num]=feature1.loc[i]
        elif b[j]== False:
            feature.loc[num]=feature2.loc[b_in[j]]
            j=j+1
        num=num+1
    return feature



def selected_by_elastic_net_boot(result_df,bt,num,l1_ratio):
    bootstrap=bt

    feature_vector=result_df.iloc[:-1,0].values

    df=result_df.iloc[:,1:]
    df.fillna(0,inplace=True)

    df_t=df.transpose()
    inp=df_select(df_t,len(df_t))

    feature1=feature_by_elastic_net(inp,l1_ratio,feature_vector)

    for i in range(bootstrap):
        inp=df_select(df_t,len(df_t))

        feature2=feature_by_elastic_net(inp,l1_ratio,feature_vector)
        feature1=compare_cpg(feature1,feature2)

        sys.stdout.write('bootstrap '+'\r{0}'.format(i)+' / '+str(bootstrap)+' complete!')
        sys.stdout.flush()

    boot_feature=pd.DataFrame(columns=['Composite Element REF','count'])
    boot_feature=feature1[feature1['count']>num]

    return boot_feature
