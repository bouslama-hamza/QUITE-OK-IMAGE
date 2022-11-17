# Quite OK Image Encoder
<a name="readme-top"></a>
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]

<!-- logo-->
<br />
<div align="center">
  <a href="https://github.com/othneildrew/Best-README-Template">
    <img src="images/download.png" alt="Logo">
  </a>

  <h3 align="center">Quite Ok Image Format</h3>

  <p align="center">
    Creating a New Image Encoder with Light Speed Technologie And JAVA implimenation
    <br />
    <a href="https://github.com/bouslama-hamza"><strong>Explore the docs</strong></a>
    <br />
    <br />
  </p>
</div>

<!-- ABOUT THE PROJECT -->
## 1 About The Project

A two-dimensional array of pixels, or an array of integer values in RGBA format, can be used to represent a picture. Exact pixel storage may soon become expensive in terms of space when the image is huge.

The **Quite Ok Image** format aims to more efficiently encode the information present in an image. It is based on the notion that adjacent pixels in an image frequently have colors that are identical to or very similar to one another.

The encoding algorithm's core principles are summarized in the sections below. The purpose of this is to give you a general idea; the description of the project's tasks will include implementation details. Likewise, refer to the slides presented in class.

## 2 Image decomposition
 For reasons of optimization and simplification of the data path, each image is first decomposed, pixel by pixel, according to the R, G, B and A channels (here to simplify the decomposition only according to R, G and B is shown):

![Product Name Screen Shot][decompostion]

Then, each of the two-dimensional arrays associated with a channel is "linearized" (the rows are simply arranged end to end) so that it can be traversed as a one-dimensional array

![Product Name Screen Shot][arrays]

Encoding The encoding algorithm memorizes the current index and the previous index at all times as it traverses the arrays in a single pass from left to right.

![Product Name Screen Shot][memorizes]

Different sorts of blocks are used to encode the information depending on the value of the pixels at these indices. The QOI format standard lists six different types of blocks. For instance, the block QOI OP RUN is used to encode the quantity of times a given pixel is repeated:

![Product Name Screen Shot][sorts]

Therefore, we will only store the number n instead of storing n instances of the same pixel. Each sort of block is, in fact, recognized by a unique tag (which will allow decoding).

The last 64 pixels seen during encoding are also kept in a hash table. A "QOI OP INDEX" block, which stores the index of the pixel in the hash table, will encode a pixel instead of explicitly encoding it if it happens to appear in the image again. This is much more succinct:

![Product Name Screen Shot][last]

Only the pixels which do not result in a collision are encoded using a block of type "QOI OP INDEX," which determines the index of the pixel in the hash table.


<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!--  BUILT WITH -->
## 3 Built With

There are numerous technologies employed in this project's final product, most of work is being done using **Java** implimentation.

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- GETTING STARTED -->
## 4 Getting Started

_The project's concept may seem a little sophisticated, but the steps for getting started are quite simpler. We may summarize them as follows.._

1. Make sure that connection is really istablished
2. Clone the repo
   ```
   git clone https://github.com/bouslama-hamza/QUITE-OK-IMAGE.git
   ```
   
3. Install Java packages
   ```
   sudo apt install opt-jdk-17
   ```
   
4. Make Sure all assert are working prefectly ,easly go to setting and enable des asserts in the vmARG
   ```
   "vmARG" : "-ea"
   ```
  

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- USAGE EXAMPLES -->
## 5 Explination

In general the project is based on multiple function that work on simple purpose to end up with the last format.

and in each function , we use the ```assert ``` parametre to make sure that a spesific function is working , else its return an error .

in the end , to function used are the most important once , that based on decoder and encoder of the algorithm.

you gonna find in the reference multiple result of the QOI format , you can make a decision based on the size between PNG and QOI .

Note : you can try your own type of image using a specefic function that are declared in the ```Main.java```

   ```java
      String image_test = "random";
      Helper.writeImage(image_test+".png",QOIDecoder.decodeQoiFile(Helper.read("res/"+image_test+".qoi")));
      Diff.diff("references/"+image_test+".png", "res/"+image_test+".png");
   ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- CONTACT -->
## Contact

Hamza Bouslama - [ham.bousa98@gmail.com](ham.bousa98@gmail.com)

Project Link: [https://github.com/bouslama-hamza/QUITE-OK-IMAGE.git](https://github.com/bouslama-hamza/QUITE-OK-IMAGE.git)

linkedin Link : [Hamza Bouslama UM6P](https://www.linkedin.com/in/hamza-bouslama-523969176/)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/othneildrew/Best-README-Template.svg?style=for-the-badge

[contributors-url]: https://github.com/othneildrew/bouslama-hamza/graphs/contributors

[forks-shield]: https://img.shields.io/github/forks/othneildrew/Best-README-Template.svg?style=for-the-badge

[forks-url]: https://github.com/othneildrew/bouslama-hamza/network/members

[stars-shield]: https://img.shields.io/github/stars/othneildrew/Best-README-Template.svg?style=for-the-badge

[stars-url]: https://github.com/othneildrew/bouslama-hamza/stargazers

[issues-shield]: https://img.shields.io/github/issues/othneildrew/Best-README-Template.svg?style=for-the-badge

[issues-url]: https://github.com/othneildrew/Best-README-Template/issues

[license-shield]: https://img.shields.io/github/license/othneildrew/Best-README-Template.svg?style=for-the-badge

[license-url]: https://github.com/othneildrew/Best-README-Template/blob/master/LICENSE.txt

[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555

[linkedin-url]: https://www.linkedin.com/in/hamza-bouslama-523969176/

[Java-com]: https://img.shields.io/badgeJava-0769ADstyle=for-the-badge&logo=Java&logoColor=white

[decompostion]: images/decomposition.png
[arrays]: images/arrays.png
[memorizes]: images/memories.png
[sorts]: images/sort.png
[last]: images/last.png