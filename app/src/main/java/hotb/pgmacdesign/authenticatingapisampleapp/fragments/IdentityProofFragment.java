package hotb.pgmacdesign.authenticatingapisampleapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hotb.pgmacdesign.authenticatingapisampleapp.R;
import hotb.pgmacdesign.authenticatingapisampleapp.activities.MainActivity;
import hotb.pgmacdesign.authenticatingapisampleapp.interfaces.MainActivityListener;
import hotb.pgmacdesign.authenticatingapisampleapp.misc.Constants;
import hotb.pgmacdesign.authenticatingapisampleapp.misc.MyApplication;
import hotb.pgmacdesign.authenticatingsdk.datamodels.AuthenticatingException;
import hotb.pgmacdesign.authenticatingsdk.datamodels.QuizObjectHeader;
import hotb.pgmacdesign.authenticatingsdk.datamodels.SimpleResponseObj;
import hotb.pgmacdesign.authenticatingsdk.datamodels.VerifyQuizObj;
import hotb.pgmacdesign.authenticatingsdk.interfaces.OnTaskCompleteListener;
import hotb.pgmacdesign.authenticatingsdk.networking.AuthenticatingAPICalls;
import hotb.pgmacdesign.authenticatingsdk.networking.AuthenticatingConstants;



/**
 * Created by pmacdowell on 2017-07-27.
 */

public class IdentityProofFragment extends Fragment implements View.OnClickListener {

    private static IdentityProofFragment instance;

    private MainActivity activity;
    private MainActivityListener listener;

    private Button identityproof_top_button, identityproof_bottom_button, identityproof_nonusa_verify;
    private ListView identityproof_listview;

    private QuizListAdapter adapter;
    private QuizObjectHeader.QuizObject quizObject;
    private List<QuizObjectHeader.QuizQuestion> questions;
    private List<VerifyQuizObj.Answer> answers;
    private String quizId, transactionId, responseUniqueId;
    private boolean haveTestQuestions, bottomButtonIsShowing, topButtonIsShowing;

    public static IdentityProofFragment getInstance(MainActivity activity, MainActivityListener listener){
        if(instance == null){
            instance = new IdentityProofFragment(activity, listener);
        }
        return instance;
    }

    public IdentityProofFragment(){};

    public IdentityProofFragment(MainActivity activity, MainActivityListener listener){
        this.activity = activity;
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariables();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.identityproof_fragment, container, false);
        initUI(view);
        adjustViewVisibilities();
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Setup Variables
     */
    private void initVariables(){
        haveTestQuestions = bottomButtonIsShowing = topButtonIsShowing = false;


    }

    /**
     * Setup UI
     * @param view
     */
    private void initUI(View view) {
        identityproof_top_button = (Button) view.findViewById(
                R.id.identityproof_top_button);
        identityproof_bottom_button = (Button) view.findViewById(
                R.id.identityproof_bottom_button);
        identityproof_nonusa_verify = (Button) view.findViewById(
                R.id.identityproof_nonusa_verify);
        identityproof_listview = (ListView) view.findViewById(
                R.id.identityproof_listview);

        identityproof_top_button.setOnClickListener(this);
        identityproof_bottom_button.setOnClickListener(this);
        identityproof_nonusa_verify.setOnClickListener(this);

    }

    /**
     * Title says it all
     */
    private void adjustViewVisibilities(){
        if(haveTestQuestions){
            identityproof_top_button.setVisibility(View.GONE);
            topButtonIsShowing = false;
            identityproof_bottom_button.setVisibility(View.VISIBLE);
            bottomButtonIsShowing = true;
            identityproof_listview.setVisibility(View.VISIBLE);
        } else {
            identityproof_top_button.setVisibility(View.VISIBLE);
            topButtonIsShowing = true;
            identityproof_bottom_button.setVisibility(View.GONE);
            bottomButtonIsShowing = false;
            identityproof_listview.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.identityproof_nonusa_verify:
                listener.showOrHideLoadingAnimation(true);
                AuthenticatingAPICalls.authenticateProfile(
                        new OnTaskCompleteListener() {
                       @Override
                       public void onTaskComplete(Object o, int i) {
                           listener.showOrHideLoadingAnimation(false);
                           if(i == AuthenticatingConstants.TAG_SIMPLE_RESPONSE_OBJ){
                               SimpleResponseObj s = (SimpleResponseObj) o;
                               if(s != null) {
                                   SimpleResponseObj.SimpleResponse ss = s.getSimpleResponse();
                                   if(ss != null){
                                       if(ss.getSuccess()){
                                           //Test was successfully sent.
                                       } else {
                                           //Test was not successfully sent
                                       }
                                   }
                               }
                           }
                       }
                   }, Constants.SAMPLE_COMPANY_API_KEY,
                        MyApplication.getSharedPrefsInstance().getString(
                                Constants.ACCESS_CODE, ""));
                break;

            case R.id.identityproof_top_button:
                listener.showOrHideLoadingAnimation(true);
                AuthenticatingAPICalls.getQuiz(
                        new OnTaskCompleteListener() {
                            @Override
                            public void onTaskComplete(Object o, int i) {
                                listener.showOrHideLoadingAnimation(false);
                                if (i == AuthenticatingConstants.TAG_ERROR_RESPONSE) {
                                    AuthenticatingException e = (AuthenticatingException) o;
                                    L.Toast(getActivity(), "An error has Occurred: " +
                                            e.getAuthErrorString());
                                } else if (i == AuthenticatingConstants.TAG_QUIZ_QUESTIONS_HEADER) {
                                    QuizObjectHeader quizes = (QuizObjectHeader) o;
                                    quizObject = quizes.getQuizObject();
                                    if(quizObject != null){
                                        questions = quizObject.getQuizQuestions();
                                    }
                                    if(questions != null){
                                        if(questions.size() > 0){
                                            haveTestQuestions = true;
                                            adapter = new QuizListAdapter(getActivity(), questions);
                                            identityproof_listview.setAdapter(adapter);
                                        } else {
                                            haveTestQuestions = false;
                                            L.Toast(getActivity(), "We could not find you with the data you provided. Please go to update user and include your social so we can successully get your tests.");
                                        }
                                    } else {
                                        haveTestQuestions = false;
                                        L.Toast(getActivity(), "We could not find you with the data you provided. Please go to update user and include your social so we can successully get your tests.");
                                    }

                                    quizId = quizObject.getQuizId();
                                    transactionId = quizObject.getTransactionId();
                                    responseUniqueId = quizObject.getResponseUniqueId();

                                    adjustViewVisibilities();
                                }
                            }
                        }, Constants.SAMPLE_COMPANY_API_KEY,
                        MyApplication.getSharedPrefsInstance().getString(Constants.ACCESS_CODE, "")
                );
                break;

            case R.id.identityproof_bottom_button:
                answers = adapter.getAnswers();
                int minNeeded = 1;
                if(questions != null){
                    if(questions.size() > 0){
                        minNeeded = questions.size();
                    }
                }
                if(answers == null || answers.size() < minNeeded){
                    L.Toast(getActivity(), "Please Select All answers before submitting!");
                    return;
                }
                listener.showOrHideLoadingAnimation(true);
                AuthenticatingAPICalls.verifyQuiz(
                        new OnTaskCompleteListener() {
                            @Override
                            public void onTaskComplete(Object o, int i) {
                                listener.showOrHideLoadingAnimation(false);

                                //Regardless of pass or fail, make sure to clear data
                                questions = new ArrayList<QuizObjectHeader.QuizQuestion>();
                                answers = new ArrayList<VerifyQuizObj.Answer>();
                                adapter.clearData();
                                haveTestQuestions = false;
                                adjustViewVisibilities();

                                if (i == AuthenticatingConstants.TAG_ERROR_RESPONSE) {
                                    AuthenticatingException e = (AuthenticatingException) o;
                                    L.Toast(getActivity(), "An error has Occurred: " +
                                            e.getAuthErrorString());
                                } else if (i == AuthenticatingConstants.TAG_SIMPLE_RESPONSE_OBJ) {
                                    SimpleResponseObj s = (SimpleResponseObj) o;
                                    if(s != null) {
                                        SimpleResponseObj.SimpleResponse ss = s.getSimpleResponse();
                                        if(ss != null){
                                            boolean passedTest = ss.getSuccess();
                                            //Do whatever from here
                                        }
                                    }
                                }
                            }
                        }, Constants.SAMPLE_COMPANY_API_KEY,
                        MyApplication.getSharedPrefsInstance().getString(Constants.ACCESS_CODE, ""),
                        answers.toArray(new VerifyQuizObj.Answer[answers.size()]),
                        quizId, transactionId, responseUniqueId
                );
                break;
        }
    }


    private class QuizListAdapter extends ArrayAdapter<QuizObjectHeader.QuizQuestion> implements View.OnClickListener {

        private Context context;
        private List<QuizObjectHeader.QuizQuestion> mListObjects;
        private List<VerifyQuizObj.Answer> answers;
        private QuizObjectHeader.Choice[] choices;
        private Map<String, String> answersMap;

        public QuizListAdapter(@NonNull Context context, List<QuizObjectHeader.QuizQuestion> mListObjects) {
            super(context, R.layout.recyclerview_identity_quiz, mListObjects);
            this.context = context;
            this.mListObjects = mListObjects;
            this.answers = new ArrayList<>();
            this.answersMap = new HashMap<>();
        }

        public void clearData(){
            this.mListObjects = new ArrayList<>();
            notifyDataSetChanged();
        }

        @Override
        public int getViewTypeCount() {
            if(this.mListObjects == null){
                this.mListObjects = new ArrayList<>();
            }
            if(this.mListObjects.size() <= 0){
                return 0;
            } else {
                return this.mListObjects.size();
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.recyclerview_identity_quiz, parent, false);
                holder.identity_quiz_question_tv = (TextView) convertView.findViewById(
                        R.id.identity_quiz_question_tv);
                holder.identity_quiz_radiobutton_1 = (RadioButton) convertView
                        .findViewById(R.id.identity_quiz_radiobutton_1);
                holder.identity_quiz_radiobutton_2 = (RadioButton) convertView
                        .findViewById(R.id.identity_quiz_radiobutton_2);
                holder.identity_quiz_radiobutton_3 = (RadioButton) convertView
                        .findViewById(R.id.identity_quiz_radiobutton_3);
                holder.identity_quiz_radiobutton_4 = (RadioButton) convertView
                        .findViewById(R.id.identity_quiz_radiobutton_4);
                holder.identity_quiz_radio_group = (RadioGroup) convertView.findViewById(
                        R.id.identity_quiz_radio_group);

                holder.identity_quiz_radiobutton_1.setOnClickListener(this);
                holder.identity_quiz_radiobutton_2.setOnClickListener(this);
                holder.identity_quiz_radiobutton_3.setOnClickListener(this);
                holder.identity_quiz_radiobutton_4.setOnClickListener(this);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {
                choices = mListObjects.get(position).getChoice();
            } catch (Exception e){
                return convertView;
            }
            if(choices == null){
                return convertView;
            }
            if(choices.length <= 0){
                return convertView;
            }
            holder.identity_quiz_radiobutton_1.setText(choices[0].getText());
            holder.identity_quiz_radiobutton_2.setText(choices[1].getText());
            holder.identity_quiz_radiobutton_3.setText(choices[2].getText());
            holder.identity_quiz_radiobutton_4.setText(choices[3].getText());
            holder.identity_quiz_question_tv.setText(mListObjects.get(position).getText());
            holder.identity_quiz_radio_group.setTag(position);
            holder.identity_quiz_radio_group.setOnCheckedChangeListener(
                    new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            String questionId = null, choiceId = null;
                            switch (checkedId) {
                                case R.id.identity_quiz_radiobutton_1:

                                    choiceId = mListObjects.get(position).getChoice()[0].getChoiceId();
                                    questionId = mListObjects.get(position).getQuestionId();
                                    break;

                                case R.id.identity_quiz_radiobutton_2:
                                    choiceId = mListObjects.get(position).getChoice()[1].getChoiceId();
                                    questionId = mListObjects.get(position).getQuestionId();
                                    break;

                                case R.id.identity_quiz_radiobutton_3:
                                    choiceId = mListObjects.get(position).getChoice()[2].getChoiceId();
                                    questionId = mListObjects.get(position).getQuestionId();
                                    break;

                                case R.id.identity_quiz_radiobutton_4:
                                    choiceId = mListObjects.get(position).getChoice()[3].getChoiceId();
                                    questionId = mListObjects.get(position).getQuestionId();
                                    break;

                                default:
                                    break;
                            }
                            setAnswer(questionId, choiceId);
                        }
                    });
            return convertView;
        }

        @Override
        public void onClick(View v) {
            //Required for the click functionality to not thrown an exception
        }

        private class ViewHolder {

            private TextView identity_quiz_question_tv;
            private RadioGroup identity_quiz_radio_group;
            private RadioButton identity_quiz_radiobutton_1, identity_quiz_radiobutton_2,
                    identity_quiz_radiobutton_3, identity_quiz_radiobutton_4;

        }

        public void setAnswer(String questionId, String choiceId) {
            answersMap.put(questionId, choiceId);
        }

        public List<VerifyQuizObj.Answer> getAnswers() {
            answers = new ArrayList<>();
            for(Map.Entry<String, String> map : answersMap.entrySet()){
                String key = map.getKey();
                String value = map.getValue();
                VerifyQuizObj.Answer ans = new VerifyQuizObj.Answer();
                ans.setQuestionId(key);
                ans.setChoiceId(value);
                answers.add(ans);
            }
            return answers;
        }

    }

}
