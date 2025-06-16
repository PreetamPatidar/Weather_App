import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGui extends JFrame {

    private JSONObject weatherData;
    public WeatherAppGui(){
        //App title
        super("weather App");

        //configure gui to end the program process once it has been closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //set the size of our gui (in pixels)
        setSize(450, 650);

        //load our gui at the center of the screen
        setLocationRelativeTo(null);

        // make our layout manager null to manually position our components within the gui
        setLayout(null);

        //prevent any resize of our gui
        setResizable(false);

        addGuiComponents();

    }
    private void addGuiComponents(){
        //search Field
        JTextField searchTextField = new JTextField();

        //set the location and size of our component
        searchTextField.setBounds(15, 15, 351, 45);

        //change the font style and size
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(searchTextField);



        //weather image
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        //temperature Text
        JLabel temperatureText = new JLabel("10 C°");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));

        //center the text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        //weather condition description
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0,405,450,36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        //humidity Image
        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        //humidity text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        //windSpeed Image
        JLabel windSpeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windSpeedImage.setBounds(220, 500, 74, 66);
        add(windSpeedImage);

        //windSpeed text
        JLabel windSpeedText = new JLabel("<html><b>WindSpeed</b> 10km/h</html>");
        windSpeedText.setBounds(319,500,85,55);
        windSpeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windSpeedText);

        //search button
        JButton searchButton = new JButton(loadImage("src/assets/search.png"));

        //change the cursor to a hand cursor when hovering over this button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13,47, 45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //get location from user
                String userInput = searchTextField.getText();

                //validate input - remove whitespace to ensure non-empty text
                if(userInput.replaceAll("\\s", "").length() <= 0){
                    return;
                }

                //retrieve weather data
                weatherData = WeatherApp.getWeatherData(userInput);

                //update gui

                //update weather image
                 String weatherCondition = (String) weatherData.get("weather_condition");

                 //depending on the condition, we will update the weather image that corresponds with the condition
                switch (weatherCondition) {
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;

                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));
                        break;
                }
                //update temperature text
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " C°");

                //update weather condition text
                weatherConditionDesc.setText(weatherCondition);

                //update humidity text
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> "+ humidity + "%</html>");

                //update windSpeed text
                double windSpeed = (double) weatherData.get("windspeed");
                windSpeedText.setText("<html><b>Windspeed</b> "+ windSpeed + "km/h</html>");

            }
        });
        add(searchButton);
    }

    //used tp create Image in our components
    private ImageIcon loadImage(String resourcePath){
        try{
            //read the image file from the path given
            BufferedImage image = ImageIO.read(new File(resourcePath));

            //return on image 1000 so that our component can render it
            return new ImageIcon(image);

        }catch (IOException e){
            e.printStackTrace();
        }

        System.out.println("Could not find resource");
        return null;

    }
}
