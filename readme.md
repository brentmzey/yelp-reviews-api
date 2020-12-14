# Yelp API

## Served Location

- This API is currently served on Heroku at this [root URI: "https://yelpreviews-api.herokuapp.com"](https://yelpreviews-api.herokuapp.com)

- The API can be served locally by running this Maven SpringBoot project on your local machine at "http://localhost:8080"

## Endpoints

There are two main endpoints:

- Search by Yelp Business ID
- Search with food/drink category term & a location (city name, zip code, street address, etc.)

### Search by Yelp Business ID

- /reviews/{yelpBusinessId}

- **Example**
  - /reviews/mhrW9O0O5hXGXGnEYBVoag

  - *JSON Response*

    - Returns one Yelp business search with the 3 reviews that the Yelp API returns for this business' ID

```json
{
  "businessName": "Jacques-Imo's Cafe",
  "businessId": "mhrW9O0O5hXGXGnEYBVoag",
  "imageUrl": "https://s3-media4.fl.yelpcdn.com/bphoto/anNM52mDk_L6Dt96H6OoKQ/o.jpg",
  "address": "8324 Oak St",
  "city": "New Orleans",
  "zipCode": "70118",
  "state": "LA",
  "country": "US",
  "reviews": [
    {
      "rating": 5,
      "review": "**We dined here before the pandemic in February 2020 and I'm just finishing the review now.\n\nJacques-Imo's was recommended to us by a lot of the locals and...",
      "userName": "Emily C.",
      "userAvatarUrl": "https://s3-media3.fl.yelpcdn.com/photo/mNpj1g0_qjgguGh-CnE3uA/o.jpg"
    },
    {
      "rating": 5,
      "review": "My fiancé and I had an awesome dinner at this restaurant! The customer service was great. Our server was friendly & attentive and did a great job of letting...",
      "userName": "Astrya J.",
      "userAvatarUrl": "https://s3-media1.fl.yelpcdn.com/photo/jPINYXmrYLp09geQOTUOjQ/o.jpg"
    },
    {
      "rating": 5,
      "review": "We heard about Jacques-Imo's from an Airbnb guest book. Since COVID-19, we called to make sure they were allowing dine in (they were) and headed right over....",
      "userName": "Vickie M.",
      "userAvatarUrl": null
    }
  ]
}
```

### Search by Term & Location

- /reviews?term={term}&location={location}

- **Example**
  - /reviews?term=pastry&location=milwaukee

  - *JSON Response*

    - "count" is total number of Yelp businesses that match this search
    - "limit" is the max numer of business returned by this API (starting from the top of what the Yelp API returns)
    - Returns at most 10 business responses inside the "data" array
    - Each object node inside the "data" array is one business search in the same structure as searching by Yelp business ID as the above JSON response

```json

{
  "count": 442,
  "limit": 10,
  "data": [
    {
      "businessName": "Amaranth Bakery & Cafe",
      "businessId": "pz1thL9deYZ6wLRifH600w",
      "imageUrl": "https://s3-media1.fl.yelpcdn.com/bphoto/zXGPu3hFwOsGi0AcIPvdsQ/o.jpg",
      "address": "3329 W Lisbon Ave",
      "city": "Milwaukee",
      "zipCode": "53208",
      "state": "WI",
      "country": "US",
      "reviews": [
        {
          "rating": 5,
          "review": "As I'm slowly starting to re-enter the world again, I'm being very picky about where I choose to dine (it just so happens they're across the street from...",
          "userName": "Nell B.",
          "userAvatarUrl": "https://s3-media1.fl.yelpcdn.com/photo/g8oeZpibeXk-16E3IoiILQ/o.jpg"
        },
        {
          "rating": 5,
          "review": "Ordered curbside takeaway meal a few weeks ago and it was phenomenal.   Order was ready at stated time and they brought it to the car.  We live about 15...",
          "userName": "Jeri A.",
          "userAvatarUrl": null
        },
        {
          "rating": 5,
          "review": "We had curbside supper from here two weeks ago and it was fabulous. It was ready on time was packaged beautifully and still hot when we got home. Looking...",
          "userName": "Kelli G.",
          "userAvatarUrl": null
        }
      ]
    },
    {
      "businessName": "Rocket Baby Bakery",
      "businessId": "_aC_2Oe-3CZCaRzQBdhemw",
      "imageUrl": "https://s3-media1.fl.yelpcdn.com/bphoto/FJfkQR3fWcaq1nLHF43DqQ/o.jpg",
      "address": "6822 W North Ave",
      "city": "Wauwatosa",
      "zipCode": "53213",
      "state": "WI",
      "country": "US",
      "reviews": [
        {
          "rating": 5,
          "review": "Worth every penny...yum! We took advantage of their Easter special which was take and bake cinnamon rolls due to COVID-19. I saw the promotion via...",
          "userName": "Monica H.",
          "userAvatarUrl": "https://s3-media3.fl.yelpcdn.com/photo/OFXkSjvTzqubW1g4pwYgQQ/o.jpg"
        },
        {
          "rating": 5,
          "review": "That bakery smell!\n\nI've gone to the gym down below for a few years now and have to say, the armoma that comes thru the concrete is the best worst thing for...",
          "userName": "Andy K.",
          "userAvatarUrl": "https://s3-media1.fl.yelpcdn.com/photo/oO8GkaNLUJCTdRHEUSmp8g/o.jpg"
        },
        {
          "rating": 5,
          "review": "I don't often review places outside my residential community but I must tell you about this little gem. I was invited here for a Corporate holiday event. I...",
          "userName": "Phyllis W.",
          "userAvatarUrl": "https://s3-media3.fl.yelpcdn.com/photo/62npQ-OIBi-5Cum9b3leCw/o.jpg"
        }
      ]
    },
    ...
    {
      "businessName": "Sprocket Cafe",
      "businessId": "HR6go9bqjRJh6J5OgEm-rQ",
      "imageUrl": "https://s3-media3.fl.yelpcdn.com/bphoto/8XePu9gdgZ3npZEbO7-UUw/o.jpg",
      "address": "3385 S Kinnickinnic Ave",
      "city": "Milwaukee",
      "zipCode": "53207",
      "state": "WI",
      "country": "US",
      "reviews": [
        {
          "rating": 5,
          "review": "This has become my all time favorite \"for anything delicious, place!\" The quality of their food goes above and beyond, right down to the packaging! It's all...",
          "userName": "Jayne N.",
          "userAvatarUrl": "https://s3-media4.fl.yelpcdn.com/photo/i8S0XpeMwf-ACWdayP6nPg/o.jpg"
        },
        {
          "rating": 4,
          "review": "During a visit to the south side of Milwaukee I checked my bookmarks for any breakfast places I hadn't been to and O&H was the first one in the list. I...",
          "userName": "Greg B.",
          "userAvatarUrl": "https://s3-media1.fl.yelpcdn.com/photo/u0tWTlJrmEb9hBeY7wxDUg/o.jpg"
        },
        {
          "rating": 4,
          "review": "On the list of topics for last week's random banter with my brother was such burning questions as: when was the last time we encountered such an opinion...",
          "userName": "Tricia D.",
          "userAvatarUrl": "https://s3-media1.fl.yelpcdn.com/photo/aCCigSJNsLOHwEaCAY_cHQ/o.jpg"
        }
      ]
    }
  ],
  "errors": null
}
```
